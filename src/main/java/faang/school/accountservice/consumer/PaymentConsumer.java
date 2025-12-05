package faang.school.accountservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.kafka.PaymentAuthorizationRequestDto;
import faang.school.accountservice.dto.payment.kafka.PaymentAuthorizationResponseDto;
import faang.school.accountservice.dto.payment.kafka.PaymentCancelRequestDto;
import faang.school.accountservice.dto.payment.kafka.PaymentCancelResponseDto;
import faang.school.accountservice.dto.payment.kafka.PaymentClearingRequestDto;
import faang.school.accountservice.dto.payment.kafka.PaymentClearingResponseDto;
import faang.school.accountservice.service.DispatcherBankOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final DispatcherBankOperationService dispatcherBankOperationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.topic.payments.authorization.request}",
            containerFactory = "paymentKafkaListenerContainerFactory",
            groupId = "payment-service-authorization"
    )
    public void handlePaymentListenerAuthorizationRequest(@Payload Map<String, Object> message, Acknowledgment ack) {

        PaymentAuthorizationRequestDto paymentAuthorizationRequestDto = objectMapper.convertValue(message, PaymentAuthorizationRequestDto.class);

        log.info("Message processed on Authorization! OperationId - {}, amount - {}",
                paymentAuthorizationRequestDto.transferId(), paymentAuthorizationRequestDto.amount());

        PaymentAuthorizationResponseDto result = dispatcherBankOperationService.authorizationOperation(paymentAuthorizationRequestDto);
        ack.acknowledge();
        log.info("Message from kafka commit successfully! Operation - Authorization, id - {}! Status - {}! Description - {}",
                result.transferId(), result.paymentStatus(), result.description());
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.payments.clearing.request}",
            containerFactory = "paymentKafkaListenerContainerFactory",
            groupId = "payment-service-clearing"
    )
    public void handlePaymentListenerClearingRequest(@Payload Map<String, Object> message, Acknowledgment ack) {

        PaymentClearingRequestDto paymentClearingRequestDto = objectMapper.convertValue(message, PaymentClearingRequestDto.class);
        log.info("Message processed on Clearing! OperationId - {}, amount - {}",
                paymentClearingRequestDto.transferId(), paymentClearingRequestDto.amount());

        PaymentClearingResponseDto result = dispatcherBankOperationService.clearingOperation(paymentClearingRequestDto);

        ack.acknowledge();
        log.info("Message from kafka commit successfully! Operation - Clearing, id - {}! Status - {}! Description - {}",
                result.transferId(), result.paymentStatus(), result.description());
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.payments.cancel.request}",
            containerFactory = "paymentKafkaListenerContainerFactory",
            groupId = "payment-service-cancel"
    )
    public void handlePaymentListenerCancelRequest(@Payload Map<String, Object> message, Acknowledgment ack) {

        PaymentCancelRequestDto paymentCancelRequestDto = objectMapper.convertValue(message, PaymentCancelRequestDto.class);
        log.info("Message processed on Cancel!  OperationId - {}, amount - {}",
                paymentCancelRequestDto.transferId(), paymentCancelRequestDto.amount());

        PaymentCancelResponseDto result= dispatcherBankOperationService.cancelOperation(paymentCancelRequestDto);

        ack.acknowledge();
        log.info("Message from kafka commit successfully! Operation - Cancel, id - {}! Status - {}! Description - {}",
                result.transferId(), result.paymentStatus(), result.description());
    }
}
