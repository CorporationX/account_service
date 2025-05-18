package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.account.AccountService;
import faang.school.accountservice.validation.BalanceValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final AccountService accountService;
    private final BalanceHelper balanceHelper;
    private final BalanceValidator balanceValidator;
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    /**
     * Создает новый баланс для указанного счета.
     *
     * @param accountId идентификатор счета
     */
    @Transactional
    public void createBalanceForAccount(Long accountId) {
        Account account = accountService.getAccountById(accountId);
        Balance balance = new Balance();
        balance.setAccount(account);
        balanceRepository.save(balance);
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
            balanceValidator.validateActualBalanceSufficiency(actualBalance, amount);
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
            balanceValidator.validateAuthorizedBalanceSufficiency(authorizedBalance, amount);
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
            balanceValidator.validateAuthorizedBalanceSufficiency(authorizedBalance, amount);
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
        Balance balance = getBalanceEntity(accountId);
        log.debug("Balance retrieved for account: {}", accountId);
        return balanceMapper.toViewDto(balance);
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

    public Balance getBalanceEntity(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found for authorization. Account ID: {}", accountId);
                    return new AccountNotFoundException(
                            String.format("Account not found with ID: %d", accountId));
                });
    }
}
