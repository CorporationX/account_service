package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.common.RecordNotFoundException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validator.BalanceValidator;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.UUID;

@Validated
@Service
@RequiredArgsConstructor
@Log4j2
@EnableRetry
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceValidator balanceValidator;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Balance getBalanceByAccountId(UUID accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RecordNotFoundException("Balance not found for account %s".formatted(accountId)));
    }

    @Transactional(readOnly = true)
    public Balance getBalanceByAccountNumber(String accountNumber) {
        return balanceRepository.findByAccount_AccountNumber(accountNumber)
                .orElseThrow(() -> new RecordNotFoundException("Balance not found for account with number %s".formatted(accountNumber)));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Balance createBalance(UUID accountId) {
        balanceValidator.validateBalanceNotExist(accountId);

        Account account = accountRepository.getReferenceById(accountId);

        Balance newBalance = Balance.builder()
                .account(account)
                .build();
        return balanceRepository.save(newBalance);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000)
    )
    public Balance deposit(UUID accountId, @Positive BigDecimal amount) {
        log.debug("Attempting to deposit transfer for account: {} - {}", accountId, amount);

        Balance balance = getBalanceByAccountId(accountId);

        balance.setActualAmount(balance.getActualAmount().add(amount));
        balance = balanceRepository.save(balance);

        log.debug("Deposit successfully for account {} - {}", accountId, amount);
        return balance;
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000)
    )
    public Balance authorize(UUID accountId, @Positive BigDecimal amount) {
        log.debug("Attempting to authorize transfer for account: {} - {}", accountId, amount);
        Balance balance = getBalanceByAccountId(accountId);

        balanceValidator.validateEnoughActualAmount(balance, amount);

        balance.setAuthorizedAmount(
                balance.getAuthorizedAmount().add(amount)
        );

        balance = balanceRepository.save(balance);

        log.debug("Authorized successfully for account {} - {}", accountId, amount);
        return balanceRepository.save(balance);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000)
    )
    public Balance cancelAuthorization(UUID accountId, @Positive BigDecimal amount) {
        log.debug("Attempting to cancel authorization transfer for account: {} - {}", accountId, amount);
        Balance balance = getBalanceByAccountId(accountId);

        balanceValidator.validateEnoughAuthorizedAmount(balance, amount);

        balance.setAuthorizedAmount(
                balance.getAuthorizedAmount().subtract(amount)
        );

        log.debug("Cancelled successfully for account {} - {}", accountId, amount);
        return balanceRepository.save(balance);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000)
    )
    public Balance clear(UUID accountId, @Positive BigDecimal amount) {
        log.debug("Attempting to clear transfer for account: {} - {}", accountId, amount);

        Balance balance = getBalanceByAccountId(accountId);

        balanceValidator.validateEnoughAuthorizedAmount(balance, amount);

        balance.setAuthorizedAmount(balance.getAuthorizedAmount().subtract(amount));
        balance.setActualAmount(balance.getActualAmount().subtract(amount));

        balance = balanceRepository.save(balance);

        log.debug("Cleared transfer for account: {} - {}", accountId, amount);
        return balance;
    }
}