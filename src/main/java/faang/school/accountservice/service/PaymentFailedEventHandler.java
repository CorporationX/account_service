package faang.school.accountservice.service;

import faang.school.accountservice.enums.PaymentStages;
import faang.school.accountservice.kafka.AccountProducer;
import faang.school.accountservice.model.dto.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentFailedEventHandler {

    private final RequestService requestService;
    private final AccountProducer accountProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentFailed(PaymentFailedEvent event) {
        try {
            requestService.updateStatus(
                    event.getRequestId(),
                    PaymentStages.FAILED,
                    "Ошибка авторизации: " + event.getReason()
            );

            accountProducer.sendFailed(event.getMessage());

            log.info("FAILED обработан для заявки {}: статус обновлён, сообщение отправлено", event.getRequestId());
        } catch (Exception ex) {
            log.error("Ошибка при обработке FAILED для заявки {}: {}", event.getRequestId(), ex.getMessage(), ex);
        }
    }
}