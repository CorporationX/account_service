package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mapper.BalanceMapper2;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance2;
import faang.school.accountservice.repository.BalanceRepository2;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.validation.BalanceValidator2;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService2 {
    private final AccountService accountService;
    private final BalanceHelper balanceHelper;
    private final BalanceValidator2 balanceValidator2;
    private final BalanceRepository2 balanceRepository2;
    private final BalanceMapper2 balanceMapper;

    /**
     * Создает новый баланс для указанного счета.
     *
     * @param accountId идентификатор счета
     */
    @Transactional
    public void createBalanceForAccount(Long accountId) {
        Account account = accountService.getAccountById(accountId);
        Balance2 balance2 = new Balance2();
        balance2.setAccount(account);
        balanceRepository2.save(balance2);
        log.debug("Created balance for account ID: {}", accountId);
    }

    /**
     * Резервирует (авторизует) средства на счете.
     *
     * @param accountId идентификатор счета
     * @param amount    сумма для резервирования
     * @return DTO с обновленным состоянием баланса
     */
    public BalanceViewDto authorizeAmount(Long accountId, BigDecimal amount) {
        return balanceHelper.executeBalanceOperation(accountId, balance -> {
            BigDecimal actualBalance = balance.getActualBalance();
            balanceValidator2.validateActualBalanceSufficiency(actualBalance, amount);
            balance.authorize(amount);
            log.debug("Authorized {} for account ID: {}", amount, accountId);
        });
    }

    /**
     * Подтверждает списание ранее зарезервированных средств.
     *
     * @param accountId идентификатор счета
     * @param amount    сумма для списания
     * @return DTO с обновленным состоянием баланса
     */
    public BalanceViewDto clearAuthorizedAmount(Long accountId, BigDecimal amount) {
        return balanceHelper.executeBalanceOperation(accountId, balance -> {
            BigDecimal authorizedBalance = balance.getAuthorizedBalance();
            balanceValidator2.validateAuthorizedBalanceSufficiency(authorizedBalance, amount);
            balance.clear(amount);
            log.debug("Cleared {} for account ID: {}", amount, accountId);
        });
    }

    /**
     * Отменяет резервирование средств на счете.
     *
     * @param accountId идентификатор счета
     * @param amount    сумма для отмены резервирования
     * @return DTO с обновленным состоянием баланса
     */
    public BalanceViewDto cancelAuthorization(Long accountId, BigDecimal amount) {
        return balanceHelper.executeBalanceOperation(accountId, balance -> {
            BigDecimal authorizedBalance = balance.getAuthorizedBalance();
            balanceValidator2.validateAuthorizedBalanceSufficiency(authorizedBalance, amount);
            balance.cancelAuthorization(amount);
            log.debug("Cancelled authorization {} for account ID: {}", amount, accountId);
        });
    }

    /**
     * Возвращает баланс счета, включая доступный и авторизованный балансы.
     *
     * @param accountId идентификатор счета
     * @return DTO с представлением баланса
     */
    public BalanceViewDto getBalance(Long accountId) {
        Balance2 balance2 = getBalanceEntity(accountId);
        log.debug("Balance retrieved for account: {}", accountId);
        return balanceMapper.toViewDto(balance2);
    }

    /**
     * Пополняет баланс счета на указанную сумму.
     *
     * @param accountId идентификатор счета
     * @param amount    сумма пополнения
     * @return DTO с обновленным состоянием баланса
     */
    public BalanceViewDto depositAmount(Long accountId, BigDecimal amount) {
        return balanceHelper.executeBalanceOperation(accountId, balance -> {
            balance.deposit(amount);
            log.debug("Deposited {} to account ID: {}", amount, accountId);
        });
    }

    public Balance2 getBalanceEntity(Long accountId) {
        return balanceRepository2.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found for authorization. Account ID: {}", accountId);
                    return new AccountNotFoundException(
                            String.format("Account not found with ID: %d", accountId));
                });
    }
}
