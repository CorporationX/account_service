package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.common.DataValidationException;
import faang.school.accountservice.exception.common.PreConditionFailedException;
import faang.school.accountservice.exception.common.RecordNotFoundException;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountService accountService;

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

    public Balance createBalance(UUID accountId) {
        return createBalance(accountService.getById(accountId));
    }

    @Transactional
    public Balance createBalance(Account account) {
        validateBalanceNotExist(account.getId());

        Balance newBalance = Balance.builder()
                .account(account)
                .build();
        return balanceRepository.save(newBalance);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 250)
    )
    public Balance enroll(UUID accountId, BigDecimal amount) {
        validateAmount(amount);

        Balance balance = getBalanceByAccountId(accountId);
        balance.setActualAmount(balance.getActualAmount().add(amount));
        return balanceRepository.save(balance);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 250)
    )
    public Balance authorize(UUID accountId, BigDecimal amount) {
        validateAmount(amount);

        Balance balance = getBalanceByAccountId(accountId);

        validateEnoughActualAmount(balance, amount);

        balance.setAuthorizedAmount(
                balance.getAuthorizedAmount().add(amount)
        );
        balance.setActualAmount(
                balance.getActualAmount().subtract(amount)
        );
        return balanceRepository.save(balance);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 250)
    )
    public Balance cancelAuthorization(UUID accountId, BigDecimal amount) {
        validateAmount(amount);

        Balance balance = getBalanceByAccountId(accountId);

        validateEnoughAuthorizedAmount(balance, amount);

        balance.setAuthorizedAmount(
                balance.getAuthorizedAmount().subtract(amount)
        );
        balance.setActualAmount(
                balance.getActualAmount().add(amount)
        );
        return balanceRepository.save(balance);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 250)
    )
    public Balance clear(UUID accountId, BigDecimal amount) {
        validateAmount(amount);

        Balance balance = getBalanceByAccountId(accountId);

        validateEnoughAuthorizedAmount(balance, amount);

        balance.setAuthorizedAmount(balance.getAuthorizedAmount().subtract(amount));
        return balanceRepository.save(balance);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new DataValidationException("Amount must be positive!");
        }
    }

    private void validateBalanceNotExist(UUID accountId) {
        if (balanceRepository.existsByAccountId(accountId)) {
            throw new PreConditionFailedException("Account already have assigned balance!");
        }
    }

    private void validateEnoughActualAmount(Balance balance, BigDecimal amount) {
        if (balance.getActualAmount().compareTo(amount) < 0) {
            throw new PreConditionFailedException("Not enough actual funds for authorization!");
        }
    }

    private void validateEnoughAuthorizedAmount(Balance balance, BigDecimal amount) {
        if (balance.getAuthorizedAmount().compareTo(amount) < 0) {
            throw new PreConditionFailedException("Not enough authorized funds!");
        }
    }
}