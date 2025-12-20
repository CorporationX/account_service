package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.balance.Balance;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Контракт сервиса управления балансами платёжных аккаунтов.
 * <p>
 * Баланс поддерживает двухэтапные операции списания:
 * <ul>
 *   <li><b>Авторизация</b> — резервирование (удержание) суммы для последующего подтверждения операции.</li>
 *   <li><b>Клиринг (clearing/capture)</b> — подтверждение операции и списание ранее удержанной суммы.</li>
 * </ul>
 * Также поддерживаются операции пополнения, списания, получения и обновления баланса.
 */
public interface BalanceService {

    /**
     * Возвращает текущий баланс по идентификатору аккаунта.
     *
     * @param accountId идентификатор аккаунта; не {@code null}
     * @return сущность баланса {@link Balance}
     * @throws faang.school.accountservice.exception.EntityNotFoundException если баланс не найден
     */
    Balance getBalance(@NonNull UUID accountId);

    /**
     * Создаёт баланс для указанного аккаунта с начальным значением фактического (доступного/ledger — зависит от модели)
     * баланса.
     *
     * @param accountId идентификатор аккаунта; не {@code null}
     * @param amount    начальная сумма; не {@code null} и не отрицательная
     * @return созданная сущность {@link Balance}
     * @throws IllegalArgumentException если {@code amount} отрицательная
     * @throws faang.school.accountservice.exception.EntityNotFoundException если аккаунт не найден
     * @throws faang.school.accountservice.exception.DuplicateEntityException если баланс для аккаунта уже существует
     */
    Balance create(@NonNull UUID accountId, @NonNull BigDecimal amount);

    /**
     * Обновляет авторизованный и/или фактический баланс аккаунта на основе переданного DTO.
     * <p>
     * Поддерживает частичное обновление: {@code null} в поле DTO означает «не менять значение».
     *
     * @param accountId        идентификатор аккаунта, для которого обновляется баланс; не {@code null}
     * @param updateBalanceDto DTO с новыми значениями баланса; оба поля могут быть {@code null}, но не одновременно
     * @return обновлённая сущность {@link Balance}
     *
     * @throws IllegalArgumentException если оба поля DTO равны {@code null} или если одно из полей содержит
     *                                  отрицательное значение
     * @throws faang.school.accountservice.exception.EntityNotFoundException если баланс для указанного аккаунта
     *                                                                       не найден
     * @throws faang.school.accountservice.exception.InsufficientFundsException если обновление нарушает инварианты
     *                                                                          баланса (например, фактический баланс
     *                                                                          меньше авторизованного)
     */
    Balance update(@NonNull UUID accountId, @NonNull UpdateBalanceDto updateBalanceDto);

    /**
     * Авторизует (резервирует) указанную сумму на аккаунте.
     *
     * @param accountId идентификатор аккаунта; не {@code null}
     * @param amount    сумма для авторизации; не {@code null} и не отрицательная
     * @return обновлённая сущность {@link Balance}
     *
     * @throws IllegalArgumentException если {@code amount} отрицательная
     * @throws faang.school.accountservice.exception.EntityNotFoundException если баланс не найден
     * @throws faang.school.accountservice.exception.InsufficientFundsException если недостаточно средств для
     *                                                                          авторизации
     */
    Balance authorize(@NonNull UUID accountId, @NonNull BigDecimal amount);

    /**
     * Выполняет клиринг (clearing/capture) ранее авторизованной суммы: подтверждает операцию и списывает удержание.
     *
     * @param accountId идентификатор аккаунта; не {@code null}
     * @param amount    сумма клиринга; не {@code null} и не отрицательная
     * @return обновлённая сущность {@link Balance}
     *
     * @throws IllegalArgumentException если {@code amount} отрицательная
     * @throws faang.school.accountservice.exception.EntityNotFoundException если баланс не найден
     * @throws faang.school.accountservice.exception.InsufficientFundsException если удержанных средств недостаточно
     *                                                                          для клиринга
     */
    Balance clearing(@NonNull UUID accountId, @NonNull BigDecimal amount);

    /**
     * Пополняет фактический баланс аккаунта на указанную сумму.
     *
     * @param accountId идентификатор аккаунта; не {@code null}
     * @param amount    сумма пополнения; не {@code null} и не отрицательная
     * @return обновлённая сущность {@link Balance}
     *
     * @throws IllegalArgumentException если {@code amount} отрицательная
     * @throws faang.school.accountservice.exception.EntityNotFoundException если баланс не найден
     */
    Balance topUpActualBalance(@NonNull UUID accountId, @NonNull BigDecimal amount);

    /**
     * Списывает указанную сумму с фактического (доступного) баланса аккаунта.
     *
     * @param accountId идентификатор аккаунта; не {@code null}
     * @param amount    сумма списания; не {@code null} и не отрицательная
     * @return обновлённая сущность {@link Balance}
     *
     * @throws IllegalArgumentException если {@code amount} отрицательная
     * @throws faang.school.accountservice.exception.EntityNotFoundException если баланс не найден
     * @throws faang.school.accountservice.exception.InsufficientFundsException если недостаточно средств для списания
     */
    Balance withdrawActualBalance(@NonNull UUID accountId, @NonNull BigDecimal amount);
}