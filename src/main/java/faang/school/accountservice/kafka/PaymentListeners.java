package faang.school.accountservice.kafka;

import faang.school.accountservice.model.dto.PaymentMessageDto;
import faang.school.accountservice.service.AccountBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Слушатель Kafka-сообщений для AccountBalanceService.
 * <p>
 * Обрабатывает события платежей, приходящие из PaymentService:
 * - AUTHORIZATION — запрос на авторизацию платежа
 * - CANCEL — отмена платежа
 * - CLEARING — подтверждение и списание платежа
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentListeners {

    /**
     * Сервис для работы с аккаунтами пользователя.
     */
    private final AccountBalanceService service;

    /**
     * Обрабатывает сообщения авторизации платежа.
     *
     * @param message DTO с информацией о платеже
     */
    @KafkaListener(topics = "${app.kafka.topics.authorization}",
            groupId = "account-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleAuthorization(PaymentMessageDto message) {
        log.info("Получено сообщение AUTHORIZATION: {}", message);
        service.processAuthorization(message);
    }

    /**
     * Обрабатывает сообщения отмены платежа.
     *
     * @param message DTO с информацией о платеже
     */
    @KafkaListener(topics = "${app.kafka.topics.cancel}",
            groupId = "account-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleCancel(PaymentMessageDto message) {
        log.info("Получено сообщение CANCEL: {}", message);
        service.processCancel(message);
    }

    /**
     * Обрабатывает сообщения подтверждения и списания платежа.
     *
     * @param message DTO с информацией о платеже
     */
    @KafkaListener(topics = "${app.kafka.topics.clearing}",
            groupId = "account-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleClearing(PaymentMessageDto message) {
        log.info("Получено сообщение CLEARING: {}", message);
        service.processClearing(message);
    }
}