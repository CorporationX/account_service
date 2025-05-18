package faang.school.accountservice.validation;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountAlreadyClosedException;
import faang.school.accountservice.exception.AccountOperationConflictException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Компонент для валидации операций над счетами.
 * Проверяет корректность изменения статуса счета, блокировки, разблокировки и закрытия.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountValidator {
    private final BalanceService balanceService;

    /**
     * Проверяет, что новый статус отличается от текущего.
     *
     * @param account   счет для проверки
     * @param newStatus новый статус счета
     * @throws AccountOperationConflictException если счет уже находится в указанном статусе
     */
    public void validateStatus(Account account, AccountStatus newStatus) {
        if (account.getAccountStatus() == newStatus) {
            log.error("Account is already in {} state for account ID: {}", newStatus, account.getId());
            throw new AccountOperationConflictException("Account is already in the requested state");
        }
    }

    /**
     * Проверяет возможность блокировки счета.
     *
     * @param account счет для проверки
     * @throws AccountAlreadyClosedException     если счет закрыт
     * @throws AccountOperationConflictException если счет уже заблокирован
     */
    public void validateBlock(Account account) {
        validateNotClosed(account);
        validateStatus(account, AccountStatus.BLOCKED);
    }

    /**
     * Проверяет возможность разблокировки счета.
     *
     * @param account счет для проверки
     * @throws AccountAlreadyClosedException     если счет закрыт
     * @throws AccountOperationConflictException если счет не заблокирован
     */
    public void validateUnblock(Account account) {
        validateNotClosed(account);
        if (account.getAccountStatus() != AccountStatus.BLOCKED) {
            log.error("Account ID {} is not blocked, cannot unblock", account.getId());
            throw new AccountOperationConflictException("Account is not blocked, cannot unblock");
        }
    }

    /**
     * Проверяет возможность закрытия счета.
     *
     * @param account счет для проверки
     * @throws AccountAlreadyClosedException     если счет уже закрыт
     * @throws AccountOperationConflictException если баланс счета не равен нулю
     */
    public void validateClose(Account account) {
        validateNotClosed(account);
        BalanceViewDto balanceViewDto = balanceService.getBalance(account.getId());
        BigDecimal availableBalance = balanceViewDto.getActualBalance();
        if (availableBalance.compareTo(BigDecimal.ZERO) != 0) {
            log.error("Attempt to close account with non-zero balance for account ID: {}", account.getId());
            throw new AccountOperationConflictException("Cannot close account with non-zero balance");
        }
    }

    /**
     * Проверяет, что счет не закрыт.
     *
     * @param account счет для проверки
     * @throws AccountAlreadyClosedException если счет закрыт
     */
    public void validateNotClosed(Account account) {
        validateStatus(account, AccountStatus.CLOSED);
    }
}