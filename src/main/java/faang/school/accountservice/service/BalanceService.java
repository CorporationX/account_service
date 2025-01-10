package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.InsufficientBalanceException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final AccountService accountService;
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    public BalanceDto authorize(String accountNumber) {
        Account account = accountService.findByAccountNumber(accountNumber);
        validateBalanceAuthorization(account);

        Balance balance = new Balance();
        balance.setAccount(account);

        balance = balanceRepository.save(balance);
        log.info("Balance for account number '{}' authorize at {}", accountNumber, LocalDateTime.now());
        return balanceMapper.toDto(balance);
    }

    @Retryable(retryFor = OptimisticLockException.class,
            maxAttemptsExpression = "@retryProperties.maxAttempts",
            backoff = @Backoff(multiplierExpression = "@retryProperties.multiplier"))
    @Transactional
    public BalanceDto depositAuthorized(String accountNumber, BigDecimal amount) {
        log.debug("Attempting to deposit authorized balance. Account Number: {}, Amount: {}", accountNumber, amount);
        Account account = accountService.findByAccountNumber(accountNumber);
        Balance balance = account.getBalance();
        balance.setAuthorizedBalance(balance.getAuthorizedBalance().add(amount));
        log.info("Authorized balance for account number '{}' deposit at {}", accountNumber, LocalDateTime.now());
        return balanceMapper.toDto(balance);
    }

    @Retryable(retryFor = OptimisticLockException.class,
            maxAttemptsExpression = "@retryProperties.maxAttempts",
            backoff = @Backoff(multiplierExpression = "@retryProperties.multiplier"))
    @Transactional
    public BalanceDto withdrawAuthorized(String accountNumber, BigDecimal amount) {
        log.debug("Attempting to withdraw authorized balance. Account Number: {}, Amount: {}", accountNumber, amount);
        Account account = accountService.findByAccountNumber(accountNumber);
        Balance balance = account.getBalance();
        BigDecimal authorizedBalance = balance.getAuthorizedBalance();
        validateWithdrawal(authorizedBalance, amount);
        balance.setAuthorizedBalance(authorizedBalance.subtract(amount));
        log.info("Authorized balance for account number '{}' withdraw at {}", accountNumber, LocalDateTime.now());
        return balanceMapper.toDto(balance);
    }

    @Retryable(retryFor = OptimisticLockException.class,
            maxAttemptsExpression = "@retryProperties.maxAttempts",
            backoff = @Backoff(multiplierExpression = "@retryProperties.multiplier"))
    @Transactional
    public BalanceDto depositActual(String accountNumber, BigDecimal amount) {
        log.debug("Attempting to deposit actual balance. Account Number: {}, Amount: {}", accountNumber, amount);
        Account account = accountService.findByAccountNumber(accountNumber);
        Balance balance = account.getBalance();
        balance.setActualBalance(balance.getActualBalance().add(amount));
        log.info("Actual balance for account number '{}' deposit at {}", accountNumber, LocalDateTime.now());
        return balanceMapper.toDto(balance);
    }

    @Retryable(retryFor = OptimisticLockException.class,
            maxAttemptsExpression = "@retryProperties.maxAttempts",
            backoff = @Backoff(multiplierExpression = "@retryProperties.multiplier"))
    @Transactional
    public BalanceDto withdrawActual(String accountNumber, BigDecimal amount) {
        log.debug("Attempting to withdraw actual balance. Balance ID: {}, Amount: {}", accountNumber, amount);
        Account account = accountService.findByAccountNumber(accountNumber);
        Balance balance = account.getBalance();
        BigDecimal actualBalance = balance.getActualBalance();
        validateWithdrawal(actualBalance, amount);
        balance.setActualBalance(actualBalance.subtract(amount));
        log.info("Actual balance for account number '{}' withdraw at {}", accountNumber, LocalDateTime.now());
        return balanceMapper.toDto(balance);
    }

    private void validateBalanceAuthorization(Account account) {
        if (account.getBalance() != null) {
            throw new DataValidationException("Balance already authorized on this account");
        }
    }

    private void validateWithdrawal(BigDecimal currentAmount, BigDecimal withdrawalAmount) {
        if (currentAmount.subtract(withdrawalAmount).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Balance is not enough for withdrawal");
        }
    }
}
