package faang.school.accountservice.service.payment;

import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.enums.PaymentOperationType;
import faang.school.accountservice.enums.PaymentStatus;
import faang.school.accountservice.publisher.PaymentMessageEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeConfirmPaymentStrategy implements PaymentOperationStrategy {
    private final PaymentMessageEventPublisher paymentMessageEventPublisher;


    @Override
    public void process(PaymentOperationDto payload, String correlationId) {
        log.info("Starting payment time confirm processing. Payload: {}", payload);
        payload.setStatus(PaymentStatus.AUTHORIZED);
        payload.setOperationType(PaymentOperationType.TIMECONFIRM);
        log.info("Continue payment time confirm processing. Payload: {}", payload);

        try {
            paymentMessageEventPublisher.publishResponse(correlationId, payload);
            log.info("Successfully published payment response. CorrelationId: {}", correlationId);
        } catch (Exception e) {
            log.error("Failed to publish payment response. CorrelationId: {}. Error: {}",
                    correlationId, e.getMessage(), e);
            throw e;
        }
        log.info("Successfully published payment response. Payload: {}", payload);
    }
}