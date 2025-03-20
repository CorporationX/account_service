package faang.school.accountservice.service.kafka_listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.config.kafka.KafkaTopicsProperties;
import faang.school.accountservice.dto.transfer_request.CancelMessageRequest;
import faang.school.accountservice.dto.transfer_request.ClearingMessageRequest;
import faang.school.accountservice.dto.transfer_request.TransferRequestDto;
import faang.school.accountservice.dto.transfer_request.TransferResponse;
import faang.school.accountservice.entity.TransferRequest;
import faang.school.accountservice.enums.transfer_request.TransferStatus;
import faang.school.accountservice.exception.NonRetryableException;
import faang.school.accountservice.service.transfer.TransferService;
import faang.school.accountservice.service.transfer.TransferValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaTransferListener {
    private final KafkaTopicsProperties topicsProperties;
    private final ObjectMapper objectMapper;
    private final TransferService transferService;
    private final TransferValidator paymentValidator;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "${spring.kafka.topics.transfer-request}")
    public void listenNewAuthRequest(ConsumerRecord<String, Object> kafkaEvent) {
        TransferRequestDto request = parseRequest(kafkaEvent, TransferRequestDto.class);

        boolean isExists = transferService.checkPaymentForDuplicates(request.id());
        if (isExists) {
            return;
        }
        String responseTopicName = topicsProperties.getTransferResponse();

        try {
            paymentValidator.validateAccounts(request);
            transferService.authorizeTransfer(request);
        } catch (Exception e) {
            paymentValidator.handleValidationException(e, request.id(),
                    result -> sendResponse(request.id(), result, responseTopicName));
        }
    }

    @KafkaListener(topics = "${spring.kafka.topics.transfer-cancel-request}")
    public void listenCancelRequest(ConsumerRecord<String, Object> kafkaEvent) {
        String responseTopicName = topicsProperties.getTransferCancelResponse();

        handleKafkaRequest(kafkaEvent,
                CancelMessageRequest.class,
                responseTopicName,
                CancelMessageRequest::paymentId,
                transferService::cancelPayment);
    }

    @KafkaListener(topics = "${spring.kafka.topics.transfer-clearing-request}")
    public void listenClearRequest(ConsumerRecord<String, Object> kafkaEvent) {
        String responseTopicName = topicsProperties.getTransferClearingResponse();

        handleKafkaRequest(kafkaEvent,
                ClearingMessageRequest.class,
                responseTopicName,
                ClearingMessageRequest::paymentId,
                transferService::transferFunds);
    }

    private void sendResponse(UUID paymentId, TransferStatus result, String topic) {
        TransferResponse response = new TransferResponse(paymentId, result);
        kafkaTemplate.send(topic, paymentId.toString(), response);
    }

    private <T> void handleKafkaRequest(ConsumerRecord<String, Object> kafkaEvent,
                                        Class<T> requestClass,
                                        String responseTopic,
                                        Function<T, UUID> idExtractor,
                                        Consumer<TransferRequest> action) {
        T request = parseRequest(kafkaEvent, requestClass);

        UUID transferId = idExtractor.apply(request);
        TransferRequest transferRequest = paymentValidator.getTransferOrThrow(transferId,
                result -> sendResponse(transferId, result, responseTopic));

        if (transferRequest == null) {
            return;
        }

        if (transferRequest.getStatus() == TransferStatus.CANCELLED) {
            sendResponse(transferId, TransferStatus.PAYMENT_ALREADY_CANCELLED, responseTopic);
            return;
        }
        if (transferRequest.getStatus() == TransferStatus.CLEARED) {
            sendResponse(transferId, TransferStatus.PAYMENT_ALREADY_CLEARED, responseTopic);
            return;
        }

        action.accept(transferRequest);
    }

    private <T> T parseRequest(ConsumerRecord<String, Object> kafkaEvent, Class<T> clazz) {
        try {
            T request = objectMapper.convertValue(kafkaEvent.value(), clazz);
            if (request == null) {
                log.error("Parsed request is null for kafkaEvent key {}", kafkaEvent.key());
                throw new NonRetryableException("Parsed request is null");
            }
            return request;
        } catch (RuntimeException e) {
            log.error("Error mapping Kafka event to {}: {}", clazz.getSimpleName(), e.getMessage(), e);
            throw new NonRetryableException(e.getMessage());
        }
    }
}
