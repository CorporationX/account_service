package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.exception.DuplicateEntityException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.mapper.BalanceUpdateMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
     * Возвращает баланс по идентификатору аккаунта с блокировкой для последующего изменения.
     *
     * @param accountId идентификатор аккаунта, не {@code null}
     * @return сущность баланса
     * @throws EntityNotFoundException если баланс для аккаунта не найден
     */
    private Balance getBalanceAndLockByAccount(@NonNull UUID accountId) {
        return balanceRepository.findAndLockByAccountId(accountId)
            .orElseThrow(() -> new EntityNotFoundException("Balance not found for account %s".formatted(accountId)));
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
        BigDecimal actualDelta = balance.getActualBalance().subtract(amountToAuthorize);
        if (actualDelta.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("insufficient funds");
        }
        balance.setAuthBalance(balance.getAuthBalance().add(amountToAuthorize));
        balance.setActualBalance(actualDelta);
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
        validateNonNegativeAmount(actualAmount, "The actual balance replenishment amount should not be negative.");

        Balance balance = getBalanceAndLockByAccount(accountId);
        balance.setActualBalance(balance.getActualBalance().add(actualAmount));
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
     * Переводит указанную сумму из авторизованного баланса в фактический.
     *
     * @param accountId                   идентификатор аккаунта, не {@code null}
     * @param amountFromAuthorizeToActual сумма для перевода, не {@code null}, не отрицательная
     * @return обновлённый баланс
     * @throws IllegalArgumentException   если сумма отрицательная
     * @throws EntityNotFoundException    если баланс не найден
     * @throws InsufficientFundsException если на авторизованном балансе недостаточно средств
     */
    private Balance releaseAuthBalance(@NonNull UUID accountId, @NonNull BigDecimal amountFromAuthorizeToActual) {
        validateNonNegativeAmount(amountFromAuthorizeToActual,
                                  "The amountFromAuthorizeToActual parameter should not be negative.");
        Balance balance = getBalanceAndLockByAccount(accountId);
        if (balance.getAuthBalance().compareTo(amountFromAuthorizeToActual) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        balance.setActualBalance(balance.getActualBalance().add(amountFromAuthorizeToActual));
        balance.setAuthBalance(balance.getAuthBalance().subtract(amountFromAuthorizeToActual));
        return balanceRepository.save(balance);
    }

    /**
     * Обновляет авторизованный и/или фактический баланс аккаунта на основе переданного DTO.
     *
     * @param accountId        идентификатор аккаунта, для которого обновляется баланс; не {@code null}
     * @param updateBalanceDto DTO с новыми значениями баланса; оба поля могут быть {@code null}, но не одновременно
     * @return обновлённая сущность {@link Balance}
     * @throws IllegalArgumentException если оба поля DTO равны {@code null} или если одно из полей содержит
     *                                  отрицательное значение
     * @throws EntityNotFoundException  если баланс для указанного аккаунта не найден
     */
    public Balance update(@NonNull UUID accountId, @NonNull UpdateBalanceDto updateBalanceDto) {
        if (updateBalanceDto.authBalance() == null && updateBalanceDto.actualBalance() == null) {
            throw new IllegalArgumentException(
                "The authorized balance and the current actual balance cannot both be zero.");
        }
        Balance balance = getBalanceAndLockByAccount(accountId);

        validateNonNegativeAmount(updateBalanceDto.authBalance(), "The authorization balance must not be negative");
        validateNonNegativeAmount(updateBalanceDto.actualBalance(), "The actual balance should not be negative");

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