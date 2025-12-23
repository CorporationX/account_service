package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.BalanceInvariantViolationException;
import faang.school.accountservice.exception.DuplicateEntityException;
import faang.school.accountservice.exception.ServiceUnavailableException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.mapper.BalanceUpdateMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Сервис для управления балансами платёжных аккаунтов.
 * <p>
 * Обеспечивает операции получения баланса, пополнения фактического баланса, авторизации средств, очистки и внутреннего
 * обновления баланса.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceUpdateMapper balanceUpdateMapper;

    /**
     * Проверяет, достаточно ли доступных средств для операции.
     * <p>
     * В модели баланса:
     * <ul>
     *   <li>{@code actualBalance} — фактический (ledger) баланс;</li>
     *   <li>{@code authBalance} — удержанные (заблокированные) средства;</li>
     *   <li>доступный баланс рассчитывается как {@code actualBalance - authBalance}.</li>
     * </ul>
     *
     * @param balance баланс для проверки
     * @param amount  сумма операции; не должна быть отрицательной
     * @throws InsufficientFundsException если доступных средств меньше, чем {@code amount}
     */

    private void ensureSufficientAvailableBalance(Balance balance, BigDecimal amount) {
        BigDecimal available = balance.getActualBalance().subtract(balance.getAuthBalance());
        if (available.compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                "Insufficient available funds. Available: %s, Requested: %s".formatted(available, amount));
        }
    }

    /**
     * Выполняет клиринг (final capture) ранее авторизованной суммы: подтверждает списание и финализирует удержание.
     * <p>
     * Модель баланса:
     * <ul>
     *   <li>{@code authBalance} — удержанные (заблокированные) средства по авторизациям;</li>
     *   <li>{@code actualBalance} — фактический (ledger) баланс;</li>
     *   <li>доступный баланс рассчитывается как {@code actualBalance - authBalance}.</li>
     * </ul>
     * <p>
     * Операция подтверждает списание на сумму {@code amount} и освобождает остаток удержания:
     * <ul>
     *   <li>уменьшает {@code actualBalance} на {@code amount};</li>
     *   <li>уменьшает {@code authBalance} на {@code amount} и затем освобождает оставшееся удержание
     *       (после выполнения метода {@code authBalance = 0}).</li>
     * </ul>
     *
     * @param accountId идентификатор аккаунта; не {@code null}
     * @param amount    сумма списания; не {@code null} и не отрицательная
     * @return обновлённая сущность {@link Balance} после клиринга
     * @throws IllegalArgumentException   если {@code amount} отрицательная
     * @throws EntityNotFoundException    если баланс для указанного {@code accountId} не найден
     * @throws InsufficientFundsException если {@code authBalance} меньше, чем {@code amount}
     */
    public Balance clearing(@NonNull UUID accountId, @NonNull BigDecimal amount) {
        validateNonNegativeAmount(amount,
                                  "The amount of the authorized balance to "
                                      + "be written off must not be negative.");
        Balance balance = getBalanceAndLockByAccount(accountId);

        if (balance.getAuthBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                "the authorized balance %s is less than the write-off amount %s"
                    .formatted(balance.getAuthBalance(), amount));
        }

        balance.setAuthBalance(BigDecimal.ZERO);
        balance.setActualBalance(balance.getActualBalance().subtract(amount));
        return balanceRepository.save(balance);
    }

    /**
     * Получает баланс по идентификатору аккаунта с эксклюзивной блокировкой записи
     * для выполнения последующих модифицирующих операций.
     * <p>
     * Метод использует {@link jakarta.persistence.LockModeType#PESSIMISTIC_WRITE}
     * для предотвращения конкурентных изменений баланса несколькими транзакциями
     * одновременно.
     * <p>
     * В случае невозможности получить блокировку (например, при высокой конкуренции)
     * операция может быть автоматически повторена в соответствии с настройками retry:
     * <ul>
     *   <li>максимальное количество попыток задаётся параметром
     *       {@code app.balance.retry.max-attempts};</li>
     *   <li>задержка между попытками и стратегия backoff настраиваются через
     *       {@code app.balance.retry.backoff-delay} и {@code app.balance.retry.max-delay}.</li>
     * </ul>
     * <p>
     * Если после исчерпания всех попыток блокировку получить не удалось,
     * выполнение передаётся в метод {@code @Recover}, который преобразует
     * техническую ошибку конкурентного доступа в сервисное исключение.
     *
     * @param accountId идентификатор аккаунта; не {@code null}
     * @return сущность {@link Balance}, заблокированная для записи
     *
     * @throws EntityNotFoundException если баланс для указанного {@code accountId} не найден
     * @throws ServiceUnavailableException если баланс временно недоступен
     *         из-за высокой конкурентной нагрузки
     */
    @Retryable(
        retryFor = { PessimisticLockingFailureException.class },
        maxAttemptsExpression = "${app.balance.retry.max-attempts:2}",
        backoff = @Backoff(
            delayExpression = "${app.balance.retry.backoff-delay:200}",
            maxDelayExpression = "${app.balance.retry.max-delay:5000}",
            multiplier = 2.0
            )
    )
    private Balance getBalanceAndLockByAccount(@NonNull UUID accountId) {
        return balanceRepository.findAndLockByAccountId(accountId)
            .orElseThrow(() -> new EntityNotFoundException("Balance not found for account %s".formatted(accountId)));
    }

    /**
     * Обработчик восстановления, вызываемый при невозможности получить
     * pessimistic lock после исчерпания всех попыток retry.
     * <p>
     * Преобразует техническое исключение блокировки в сервисное исключение,
     * сигнализирующее о временной недоступности ресурса.
     *
     * @param e исходное исключение конкурентной блокировки
     * @param accountId идентификатор аккаунта, доступ к балансу которого не был получен
     * @return никогда не возвращает значение
     * @throws ServiceUnavailableException всегда выбрасывается
     */
    @Recover
    public Balance recover(PessimisticLockingFailureException e, UUID accountId) {
        throw new ServiceUnavailableException("No access to balance by accountId %s".formatted(accountId), e);
    }

    /**
     * Возвращает текущий баланс по идентификатору аккаунта.
     *
     * @param accountId идентификатор аккаунта, не {@code null}
     * @return сущность баланса
     * @throws EntityNotFoundException если баланс для аккаунта не найден
     */
    @Transactional(readOnly = true)
    public Balance getBalance(@NonNull UUID accountId) {
        return balanceRepository.findByAccountId(accountId)
            .orElseThrow(() -> new EntityNotFoundException("Balance not found for account %s".formatted(accountId)));
    }

    /**
     * Авторизует (резервирует) указанную сумму на аккаунте.
     *
     * @param accountId         идентификатор аккаунта, не {@code null}
     * @param amountToAuthorize сумма для авторизации, не {@code null}, не отрицательная
     * @return обновлённый баланс
     * @throws IllegalArgumentException   если сумма отрицательная
     * @throws EntityNotFoundException    если баланс не найден
     * @throws InsufficientFundsException если недостаточно средств на фактическом балансе
     */
    public Balance authorize(@NonNull UUID accountId, @NonNull BigDecimal amountToAuthorize) {
        validateNonNegativeAmount(amountToAuthorize,
                                  "The authorization balance replenishment amount should not be negative");
        Balance balance = getBalanceAndLockByAccount(accountId);
        ensureSufficientAvailableBalance(balance, amountToAuthorize);
        balance.setAuthBalance(balance.getAuthBalance().add(amountToAuthorize));
        return balanceRepository.save(balance);
    }

    /**
     * Пополняет фактический баланс аккаунта на указанную сумму.
     *
     * @param accountId    идентификатор аккаунта, не {@code null}
     * @param actualAmount сумма пополнения, не {@code null}, не отрицательная
     * @return обновлённый баланс
     * @throws IllegalArgumentException если сумма отрицательная
     * @throws EntityNotFoundException  если баланс не найден
     */
    public Balance topUpActualBalance(@NonNull UUID accountId, @NonNull BigDecimal actualAmount) {
        validateNonNegativeAmount(actualAmount, "The actual balance replenishment "
            + "amount should not be negative.");

        Balance balance = getBalanceAndLockByAccount(accountId);
        balance.setActualBalance(balance.getActualBalance().add(actualAmount));
        return balanceRepository.save(balance);
    }

    /**
     * Списывает указанную сумму с фактического баланса аккаунта.
     * <p>
     * Используемая модель баланса:
     * <ul>
     *   <li>{@code actualBalance} — фактический (ledger) баланс аккаунта;</li>
     *   <li>{@code authBalance} — удержанные (заблокированные) средства по авторизациям;</li>
     *   <li>доступный баланс рассчитывается как {@code actualBalance - authBalance}.</li>
     * </ul>
     * <p>
     * Операция уменьшает {@code actualBalance} на {@code actualAmount}. Перед списанием проверяется, что доступных
     * средств достаточно, то есть выполняется условие {@code actualBalance - authBalance >= actualAmount}.
     * <p>
     * Операция выполняется под блокировкой записи баланса (pessimistic write lock), чтобы корректно обрабатывать
     * конкурентные запросы и не допустить перерасхода.
     *
     * @param accountId    идентификатор аккаунта; не {@code null}
     * @param actualAmount сумма списания; не {@code null} и не отрицательная
     * @return обновлённая сущность {@link Balance} после списания
     * @throws IllegalArgumentException   если {@code actualAmount} отрицательная
     * @throws EntityNotFoundException    если баланс для указанного {@code accountId} не найден
     * @throws InsufficientFundsException если доступных средств недостаточно для списания (то есть
     *                                    {@code actualBalance - authBalance < actualAmount})
     */
    public Balance withdrawActualBalance(@NonNull UUID accountId, @NonNull BigDecimal actualAmount) {
        validateNonNegativeAmount(actualAmount, "The withdrawal amount should not be negative.");
        Balance balance = getBalanceAndLockByAccount(accountId);
        ensureSufficientAvailableBalance(balance, actualAmount);
        balance.setActualBalance(balance.getActualBalance().subtract(actualAmount));
        return balanceRepository.save(balance);
    }

    /**
     * Очищает авторизованный (резервный) баланс и возвращает очищенную сумму.
     *
     * @param accountId идентификатор аккаунта, не {@code null}
     * @return сумма, которая была в авторизованном балансе до очистки
     * @throws EntityNotFoundException если баланс не найден
     */
    private BigDecimal clearAuthBalance(@NonNull UUID accountId) {
        Balance balance = getBalanceAndLockByAccount(accountId);
        BigDecimal currentAuth = balance.getAuthBalance();
        balance.setAuthBalance(BigDecimal.ZERO);
        balanceRepository.save(balance);
        return currentAuth;
    }

    /**
     * Очищает фактический баланс и возвращает очищенную сумму.
     *
     * @param accountId идентификатор аккаунта, не {@code null}
     * @return сумма, которая была в фактическом балансе до очистки
     * @throws EntityNotFoundException если баланс не найден
     */
    private BigDecimal clearActualBalance(@NonNull UUID accountId) {
        Balance balance = getBalanceAndLockByAccount(accountId);
        BigDecimal currentActual = balance.getActualBalance();
        balance.setActualBalance(BigDecimal.ZERO);
        balanceRepository.save(balance);
        return currentActual;
    }

    /**
     * Освобождает часть удержания (уменьшает {@code authBalance}) на указанную сумму.
     * <p>
     * Метод изменяет состояние переданной сущности {@link Balance} и не выполняет сохранение.
     *
     * @param accountId идентификатор аккаунта, не {@code null}.
     * @param amountToRelease сумма освобождения; не {@code null} и не отрицательная
     * @return обновлённый баланс
     * @throws IllegalArgumentException   если {@code amountToRelease} отрицательная
     * @throws InsufficientFundsException если {@code authBalance} меньше, чем {@code amountToRelease}
     */
    private Balance releaseAuthBalance(@NonNull UUID accountId, @NonNull BigDecimal amountToRelease) {
        validateNonNegativeAmount(amountToRelease,
                                  "The amountFromAuthorizeToActual parameter should not be negative.");
        Balance balance = getBalanceAndLockByAccount(accountId);
        if (balance.getAuthBalance().compareTo(amountToRelease) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        balance.setAuthBalance(balance.getAuthBalance().subtract(amountToRelease));
        return balanceRepository.save(balance);
    }

    /**
     * Обновляет авторизованный и/или фактический баланс аккаунта на основе переданного DTO.
     *
     * <p>Поддерживает частичное обновление: {@code null} в поле DTO означает «не менять значение».</p>
     * <p>Дополнительно проверяет инвариант: фактический баланс не может быть меньше авторизованного
     * (то есть {@code actualBalance >= authBalance}).</p>
     *
     * @param accountId        идентификатор аккаунта, для которого обновляется баланс; не {@code null}
     * @param updateBalanceDto DTO с новыми значениями баланса; оба поля могут быть {@code null}, но не одновременно
     * @return обновлённая сущность {@link Balance}
     * @throws IllegalArgumentException           если оба поля DTO равны {@code null} или если одно из полей содержит
     *                                            отрицательное значение
     * @throws EntityNotFoundException            если баланс для указанного аккаунта не найден
     * @throws BalanceInvariantViolationException если после обновления нарушается инвариант *
     *                                            {@code actualBalance >= authBalance}
     */
    public Balance update(@NonNull UUID accountId, @NonNull UpdateBalanceDto updateBalanceDto) {
        if (updateBalanceDto.authBalance() == null && updateBalanceDto.actualBalance() == null) {
            throw new BalanceInvariantViolationException("Authorized balance and actual balance cannot both be null.");
        }

        BigDecimal newAuthBalance = updateBalanceDto.authBalance();
        BigDecimal newActualBalance = updateBalanceDto.actualBalance();

        validateNonNegativeAmount(newAuthBalance, "The authorization balance must not be negative");
        validateNonNegativeAmount(newActualBalance, "The actual balance should not be negative");

        Balance balance = getBalanceAndLockByAccount(accountId);

        if (newActualBalance == null) {
            if (balance.getActualBalance().compareTo(newAuthBalance) < 0) {
                throw new BalanceInvariantViolationException(
                    "Invariant violated: actualBalance=%s must be >= newAuthBalance=%s".formatted(
                        balance.getActualBalance(), newAuthBalance));
            }
        } else if (newAuthBalance == null) {
            if (newActualBalance.compareTo(balance.getAuthBalance()) < 0) {
                throw new BalanceInvariantViolationException(
                    "Invariant violated: newActualBalance=%s must be >= currentAuthBalance=%s".formatted(
                        newActualBalance, balance.getAuthBalance()));
            }
        } else {
            if (newActualBalance.compareTo(newAuthBalance) < 0) {
                throw new BalanceInvariantViolationException(
                    "Invariant violated: newActualBalance=%s must be >= newAuthBalance=%s".formatted(newActualBalance,
                                                                                                     newAuthBalance));
            }
        }

        balanceUpdateMapper.updateBalanceFromDto(updateBalanceDto, balance);
        return balanceRepository.save(balance);
    }

    /**
     * Создаёт новый баланс для указанного аккаунта с начальным фактическим значением.
     *
     * @param accountId идентификатор аккаунта, не {@code null}
     * @param amount    начальный фактический баланс, не {@code null}, не отрицательный
     * @return созданный баланс
     * @throws IllegalArgumentException если {@code amount} отрицателен
     * @throws EntityNotFoundException  если аккаунт не найден
     * @throws DuplicateEntityException если баланс для аккаунта уже существует
     */
    public Balance create(@NonNull UUID accountId, @NonNull BigDecimal amount) {
        validateNonNegativeAmount(amount, "The actual balance should not be negative");
        Account account = accountRepository.findById(accountId).orElseThrow(
            () -> new EntityNotFoundException(String.format("Such an account %s does not exist", accountId)));

        Balance balance = Balance.builder().account(account).actualBalance(amount).build();
        try {
            balanceRepository.save(balance);
            return balance;
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntityException(
                "Attempt to re-create the balance. A balance for the accountId %s already exists.".formatted(
                    accountId));
        }
    }

    /**
     * Проверяет, что переданная сумма не является отрицательной.
     * <p>
     * Если значение равно {@code null}, проверка не выполняется и метод завершается без выброса исключения. Это
     * поведение удобно при частичном обновлении, когда отсутствие значения трактуется как «не менять поле».
     *
     * @param amount       сумма для проверки; может быть {@code null}
     * @param errorMessage сообщение ошибки для {@link IllegalArgumentException}, используемое при отрицательном
     *                     значении суммы
     * @throws IllegalArgumentException если {@code amount} меньше нуля
     */
    private void validateNonNegativeAmount(BigDecimal amount, String errorMessage) {
        if (amount == null) {
            return;
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}