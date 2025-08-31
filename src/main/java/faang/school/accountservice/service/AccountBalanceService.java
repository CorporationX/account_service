package faang.school.accountservice.service;

import faang.school.accountservice.model.dto.AccountBalanceDto;
import faang.school.accountservice.model.dto.BalanceAuditDto;
import faang.school.accountservice.model.dto.PaymentMessageDto;
import faang.school.accountservice.model.AccountBalance;
import faang.school.accountservice.model.BalanceAudit;

import java.util.List;

/**
 * Сервис для работы с балансами аккаунтов и обработки платежных сообщений.
 * <p>
 * Обеспечивает:
 * <ul>
 *     <li>Обработку платежей в рамках Dual Message System (DMS): авторизация, клиринг, отмена.</li>
 *     <li>Управление балансами пользователей и ведение аудита всех изменений.</li>
 *     <li>Получение текущего состояния баланса и истории изменений (аудит).</li>
 * </ul>
 */
public interface AccountBalanceService {

    /**
     * Обрабатывает сообщение авторизации платежа.
     * <p>
     * Выполняет резервирование средств на балансе пользователя.
     * Создает запись в таблице аудита с соответствующей версией баланса.
     *
     * @param message объект {@link PaymentMessageDto}, содержащий данные авторизации
     */
    void processAuthorization(PaymentMessageDto message);

    /**
     * Обрабатывает сообщение об отмене платежа.
     * <p>
     * Возвращает зарезервированные средства пользователю и фиксирует изменения в аудите.
     *
     * @param message объект {@link PaymentMessageDto}, содержащий данные отмены
     */
    void processCancel(PaymentMessageDto message);

    /**
     * Обрабатывает сообщение клиринга платежа.
     * <p>
     * Списывает зарезервированные средства и фиксирует изменения в аудите.
     *
     * @param message объект {@link PaymentMessageDto}, содержащий данные клиринга
     */
    void processClearing(PaymentMessageDto message);

    /**
     * Возвращает текущий баланс аккаунта по его идентификатору.
     *
     * @param accountId идентификатор аккаунта
     * @return объект {@link AccountBalance} с текущими данными баланса
     */
    AccountBalanceDto getBalance(Long accountId);

    /**
     * Возвращает историю изменений баланса (аудит) для указанного аккаунта.
     *
     * @param accountId идентификатор аккаунта
     * @return список объектов {@link BalanceAudit}, отсортированных по времени создания
     */
    List<BalanceAuditDto> getAudit(Long accountId);
}