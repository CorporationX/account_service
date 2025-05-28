package faang.school.accountservice.service.balance;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.ConcurrentModificationException;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceServiceImpl implements BalanceService {

    private final AccountService accountService;
    private final BalanceRepository balanceRepository;

    @Transactional(readOnly = true)
    @Override
    public Balance getBalanceByAccountId(Long accountId) {
        log.info("Getting balance for accountId: {}", accountId);
        return findByAccountId(accountId);
    }

    @Transactional
    @Override
    public Balance createBalance(Long accountId, BigDecimal initialBalance) {
        log.info("Creating balance for accountId: {} with initial amount: {}", accountId, initialBalance);
        validateAmountPositive(initialBalance, "Initial balance must be positive");

        Balance balance = Balance.builder()
                .account(accountService.getAccountEntity(accountId))
                .actualBalance(initialBalance)
                .authorizedBalance(BigDecimal.ZERO)
                .build();

        return saveBalance(balance);
    }

    @Transactional
    @Override
    public Balance authorize(Long accountId, BigDecimal amount) {
        log.info("Authorizing amount: {} for accountId: {}", amount, accountId);
        validateAmountPositive(amount, "Authorization amount must be positive");

        Balance balance = findByAccountId(accountId);
        BigDecimal availableBalance = balance.getActualBalance().subtract(balance.getAuthorizedBalance());
        validateSufficientFunds(availableBalance, amount, "Insufficient available funds for authorization");

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().add(amount));
        return saveBalance(balance);
    }

    @Transactional
    @Retryable(
            value = OptimisticLockException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100))
    @Override
    public Balance clear(Long accountId, BigDecimal amount, BigDecimal authorizedAmount) {
        log.info("Clearing (settling) authorized amount: {} (from total authorized: {}) for accountId: {}", 
                amount, authorizedAmount, accountId);
        validateAmountPositive(amount, "Clear amount must be positive");
        validateAmountPositive(authorizedAmount, "Authorized amount must be positive");

        Balance balance = findByAccountId(accountId);
        validateSufficientFunds(balance.getAuthorizedBalance(), authorizedAmount, 
                "Insufficient authorized funds for clearing");
        
        if (amount.compareTo(authorizedAmount) > 0) {
            throw new IllegalArgumentException("Clear amount cannot be greater than authorized amount");
        }

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(authorizedAmount));
        balance.setActualBalance(balance.getActualBalance().subtract(amount));

        return saveBalance(balance);
    }

    @Transactional
    @Retryable(
            value = OptimisticLockException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100))
    @Override
    public Balance cancelAuthorization(Long accountId, BigDecimal amount) {
        log.info("Canceling authorization for amount: {} for accountId: {}", amount, accountId);
        validateAmountPositive(amount, "Cancel amount must be positive");

        Balance balance = findByAccountId(accountId);
        validateSufficientFunds(balance.getAuthorizedBalance(), amount, "Insufficient authorized funds to cancel");

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));
        return saveBalance(balance);
    }

    @Transactional
    @Retryable(
            value = OptimisticLockException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 100))
    @Override
    public Balance replenish(Long accountId, BigDecimal amount) {
        log.info("Replenishing balance with amount: {} for accountId: {}", amount, accountId);
        validateAmountPositive(amount, "Replenishment amount must be positive");

        Balance balance = findByAccountId(accountId);
        balance.setActualBalance(balance.getActualBalance().add(amount));

        return saveBalance(balance);
    }

    @Recover
    public Balance recover(OptimisticLockException e, Long accountId, BigDecimal amount) {
        log.error("Failed to update balance for accountId={} after retries due to concurrent modification", accountId, e);
        throw new ConcurrentModificationException("Failed to update balance due to concurrent access", e);
    }

    private Balance findByAccountId(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Balance not found for account " + accountId));
    }

    private void validateAmountPositive(BigDecimal amount, String errorMessage) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateSufficientFunds(BigDecimal availableBalance, BigDecimal amount, String errorMessage) {
        if (availableBalance.compareTo(amount) < 0) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private Balance saveBalance(Balance balance) {
        return balanceRepository.save(balance);
    }
}