package faang.school.accountservice.service;

import faang.school.accountservice.enums.PaymentStages;
import faang.school.accountservice.kafka.AccountProducer;
import faang.school.accountservice.model.dto.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Обработчик событий {@link PaymentFailedEvent}.
 * <p>
 * Этот класс слушает события FAILED платежей после завершения основной транзакции (AFTER_COMMIT),
 * обновляет статус соответствующей заявки в таблице Request и отправляет сообщение о FAILED
 * в PaymentService через Kafka.
 * <p>
 * Используется для корректного управления статусами заявок и уведомления внешних сервисов
 * о произошедшей ошибке авторизации платежа.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentFailedEventHandler {

    /** Сервис работы с заявками, используется для обновления статуса заявки */
    private final RequestService requestService;

    /** Продюсер Kafka для отправки сообщений о FAILED платежах */
    private final AccountProducer accountProducer;

    /**
     * Обрабатывает событие FAILED платежа после успешного коммита основной транзакции.
     * <p>
     * Действия метода:
     * <ol>
     *     <li>Обновляет статус заявки на {@link faang.school.accountservice.enums.PaymentStages#FAILED}</li>
     *     <li>Добавляет в детали статус описание причины ошибки авторизации</li>
     *     <li>Отправляет сообщение в PaymentService через Kafka</li>
     *     <li>Логирует успешное завершение обработки события</li>
     * </ol>
     *
     * @param event событие FAILED платежа, содержащее {@link UUID} заявки, {@link PaymentMessageDto} и причину ошибки
     */
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