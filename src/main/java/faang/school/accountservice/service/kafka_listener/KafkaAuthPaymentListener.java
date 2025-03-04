package faang.school.accountservice.service.kafka_listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.kafka.KafkaTopicsProperties;
import faang.school.accountservice.dto.auth_payment.PaymentResponse;
import faang.school.accountservice.dto.auth_payment.authorize.AuthorizationMessageRequest;
import faang.school.accountservice.dto.auth_payment.cancel.CancelMessageRequest;
import faang.school.accountservice.dto.auth_payment.clearing.ClearingMessageRequest;
import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.enums.auth_payment.PaymentProcessingResult;
import faang.school.accountservice.exception.NonRetryableException;
import faang.school.accountservice.mapper.AuthPaymentMapper;
import faang.school.accountservice.service.OutboxEventService;
import faang.school.accountservice.service.auth_payment.AuthPaymentService;
import faang.school.accountservice.service.auth_payment.AuthPaymentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaAuthPaymentListener {
    private final KafkaTopicsProperties topicsProperties;
    private final ObjectMapper objectMapper;
    private final AuthPaymentService authPaymentService;
    private final AuthPaymentValidator paymentValidator;
    private final OutboxEventService outboxService;
    private final AuthPaymentMapper authPaymentMapper;

    @KafkaListener(topics = "${spring.kafka.topics.authPaymentRequest}")
    public void listenNewAuthRequest(ConsumerRecord<String, Object> kafkaEvent) {
        AuthorizationMessageRequest request = parseRequest(kafkaEvent, AuthorizationMessageRequest.class);
        authPaymentService.checkPaymentForDuplicates(request.id());
        String responseTopicName = topicsProperties.getAuthPaymentResponse();

        try {
            paymentValidator.validateEqualAccounts(request);
            paymentValidator.validateAccountCurrencies(request);
            AuthPayment payment = authPaymentService.fetchPaymentWithAccounts(
                    authPaymentMapper.toEntity(request),
                    request.senderAccountId(),
                    request.receiverAccountId());

            authPaymentService.authorizePaymentRequest(payment, responseTopicName);
        } catch (Exception e) {
            paymentValidator.handleValidationException(e, request.id(),
                    result -> sendResponse(request.id(), result, responseTopicName));
        }
    }

    @KafkaListener(topics = "${spring.kafka.topics.authPaymentCancelRequest}")
    public void listenCancelRequest(ConsumerRecord<String, Object> kafkaEvent) {
        String responseTopicName = topicsProperties.getAuthPaymentCancelResponse();

        handleKafkaRequest(kafkaEvent,
                CancelMessageRequest.class,
                responseTopicName,
                CancelMessageRequest::paymentId,
                payment -> authPaymentService.processPayment(payment, responseTopicName,
                        () -> authPaymentService.cancelPayment(payment, responseTopicName)));
    }

    @KafkaListener(topics = "${spring.kafka.topics.authPaymentClearingRequest}")
    public void listenClearRequest(ConsumerRecord<String, Object> kafkaEvent) {
        String responseTopicName = topicsProperties.getAuthPaymentClearingResponse();

        handleKafkaRequest(kafkaEvent,
                ClearingMessageRequest.class,
                responseTopicName,
                ClearingMessageRequest::paymentId,
                payment -> authPaymentService.processPayment(payment, responseTopicName,
                        () -> authPaymentService.transferFunds(payment, responseTopicName)));
    }

    private void sendResponse(UUID paymentId, PaymentProcessingResult result, String topic) {
        PaymentResponse response = new PaymentResponse(paymentId, result);
        outboxService.save(paymentId, PaymentResponse.class, topic, response);
    }

    private <T> void handleKafkaRequest(ConsumerRecord<String, Object> kafkaEvent,
                                        Class<T> requestClass,
                                        String responseTopic,
                                        Function<T, UUID> idExtractor,
                                        Consumer<AuthPayment> action) {
        T request = parseRequest(kafkaEvent, requestClass);
        if (request == null) {
            log.error("Parsed request is null for topic {}", responseTopic);
            throw new NonRetryableException("Parsed request is null");
        }

        AuthPayment payment = paymentValidator.fetchPayment(idExtractor.apply(request),
                result -> sendResponse(idExtractor.apply(request), result, responseTopic));

        if (payment == null) {
            return;
        }
        if (paymentValidator.validatePayment(payment, result -> sendResponse(payment.getId(), result, responseTopic))) {
            return;
        }

        action.accept(payment);
    }

    private <T> T parseRequest(ConsumerRecord<String, Object> kafkaEvent, Class<T> clazz) {
        try {
            return objectMapper.convertValue(kafkaEvent.value(), clazz);
        } catch (RuntimeException e) {
            log.error("Error mapping Kafka event to {}: {}", clazz.getSimpleName(), e.getMessage(), e);
            throw new NonRetryableException(e.getMessage());
        }
    }
}
