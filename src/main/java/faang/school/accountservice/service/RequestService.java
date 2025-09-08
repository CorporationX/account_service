package faang.school.accountservice.service;

import faang.school.accountservice.enums.PaymentMessageType;
import faang.school.accountservice.enums.PaymentStages;
import faang.school.accountservice.model.dto.PaymentMessageDto;
import faang.school.accountservice.model.dto.RequestDto;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с заявками (Request).
 * <p>
 * Управляет созданием, обновлением и поиском заявок, а также обработкой сообщений
 * от платежной системы (Authorization, Cancel, Clearing).
 * <p>
 * Все методы, которые изменяют статус заявки, гарантируют корректное обновление
 * и сохранение статуса в базе данных.
 */
public interface RequestService {

    /**
     * Создаёт новую заявку в текущей транзакции.
     *
     * @param message     DTO с информацией о платеже
     * @param requestType тип заявки {@link PaymentMessageType}
     * @param lockValue   значение для блокировки заявки
     * @return DTO созданной заявки
     */
    RequestDto createRequest(PaymentMessageDto message, PaymentMessageType requestType, String lockValue);

    /**
     * Обновляет статус существующей заявки.
     *
     * @param requestId     ID заявки
     * @param newStatus     новый статус {@link PaymentStages}
     * @param statusDetails подробное описание статуса
     * @return DTO обновлённой заявки
     */
    RequestDto updateStatus(UUID requestId, PaymentStages newStatus, String statusDetails);

    /**
     * Получает заявку по её ID.
     *
     * @param requestId ID заявки
     * @return DTO заявки
     */
    RequestDto getRequest(UUID requestId);

    /**
     * Находит все открытые заявки со статусом PENDING, которые готовы для клиринга.
     *
     * @return список DTO заявок
     */
    List<RequestDto> findPendingRequestsToClear();

    /**
     * Создаёт новую заявку в отдельной транзакции (Propagation.REQUIRES_NEW).
     *
     * @param message     DTO с информацией о платеже
     * @param requestType тип заявки {@link PaymentMessageType}
     * @param lockValue   значение для блокировки заявки
     * @return DTO созданной заявки
     */
    RequestDto createRequestNewTransaction(PaymentMessageDto message, PaymentMessageType requestType, String lockValue);

    /**
     * Обновляет заявку при получении сообщения авторизации платежа.
     * <p>
     * Если заявка находится в финальном статусе (FAILED, CANCELED, CLEARED), обработка пропускается.
     *
     * @param message DTO с информацией о платеже
     */
    void handleAuthorizationMessage(PaymentMessageDto message);

    /**
     * Обновляет заявку при получении сообщения отмены платежа.
     * <p>
     * Устанавливает статус заявки в CANCELED и закрывает её.
     *
     * @param message DTO с информацией о платеже
     */
    void handleCancelMessage(PaymentMessageDto message);

    /**
     * Обновляет заявку при получении сообщения клиринга (списание платежа).
     * <p>
     * Устанавливает статус заявки в CLEARED и закрывает её.
     *
     * @param message DTO с информацией о платеже
     */
    void handleClearingMessage(PaymentMessageDto message);
}