package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.error.InsufficientFundsException;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceAuditMapper balanceAuditMapper;

    @Override
    public BalanceResponseDto getBalance(Long balanceId) {
        return balanceMapper.toBalanceResponseDto(getBalanceById(balanceId));
    }

    @Override
    public BalanceResponseDto getOrCreateBalance(Long accountId) {
        Balance balance = balanceRepository.findByAccountId(accountId)
                .orElseGet(() -> {
                    Balance newBalance = balanceRepository.save(Balance.builder().build());
                    log.info("Created balance for account {}", accountId);
                    saveBalanceAudit(newBalance);
                    log.info("Save balance-audit after created balance for account {}", accountId);
                    return newBalance;
                });
        return balanceMapper.toBalanceResponseDto(balance);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.backoff.delay}",
                    multiplierExpression = "${retry.backoff.multiplier}"))
    @Transactional
    public BalanceResponseDto updateBalance(Long balanceId, BigDecimal authorizedBalance, BigDecimal actualBalance) {
        log.info("Updating balance {}", balanceId);
        return updateBalanceById(balanceId, authorizedBalance, actualBalance);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.backoff.delay}",
                    multiplierExpression = "${retry.backoff.multiplier}"))
    @Transactional
    public BalanceResponseDto topUpBalance(Long balanceId, BigDecimal amount) {
        Balance balance = getBalanceById(balanceId);
        balance.setActualBalance(balance.getActualBalance().add(amount));
        validateBalance(balance);
        log.info("Top-up balance {}. +{}", balanceId, amount);
        saveBalanceAudit(balance);
        return balanceMapper.toBalanceResponseDto(balanceRepository.save(balance));
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.backoff.delay}",
                    multiplierExpression = "${retry.backoff.multiplier}"))
    @Transactional
    public BalanceResponseDto writeOffFunds(Long balanceId, BigDecimal amount) {
        Balance balance = getBalanceById(balanceId);
        balance.setActualBalance(balance.getActualBalance().subtract(amount));
        validateBalance(balance);
        saveBalanceAudit(balance);
        log.info("Write-off from balance {}. -{}", balanceId, amount);
        return balanceMapper.toBalanceResponseDto(balanceRepository.save(balance));
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.backoff.delay}",
                    multiplierExpression = "${retry.backoff.multiplier}"))
    @Transactional
    public BalanceResponseDto holdFunds(Long balanceId, BigDecimal amount) {
        Balance balance = getBalanceById(balanceId);
        Balance savedBalance;
        if (balance.getActualBalance().compareTo(amount) >= 0) {
            balance.setActualBalance(balance.getActualBalance().subtract(amount));
            balance.setAuthorizedBalance(balance.getAuthorizedBalance().add(amount));
            validateBalance(balance);
            savedBalance = balanceRepository.save(balance);
            log.info("Hold funds on balance {}. -{}", balanceId, amount);
            saveBalanceAudit(savedBalance);
        } else {
            throw new InsufficientFundsException("Insufficient funds to hold! balanceId = " + balanceId);
        }
        return balanceMapper.toBalanceResponseDto(savedBalance);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.backoff.delay}",
                    multiplierExpression = "${retry.backoff.multiplier}"))
    @Transactional
    public BalanceResponseDto releaseFunds(Long balanceId, BigDecimal amount) {
        Balance balance = getBalanceById(balanceId);
        Balance savedBalance;
        if (balance.getAuthorizedBalance().compareTo(amount) >= 0) {
            balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));
            balance.setActualBalance(balance.getActualBalance().add(amount));
            validateBalance(balance);
            savedBalance = balanceRepository.save(balance);
            log.info("Release funds on balance {}. +{}", balanceId, amount);
            saveBalanceAudit(savedBalance);
        } else {
            throw new InsufficientFundsException("Insufficient held funds for release!");
        }
        return balanceMapper.toBalanceResponseDto(savedBalance);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.backoff.delay}",
                    multiplierExpression = "${retry.backoff.multiplier}"))
    @Transactional
    public BalanceResponseDto writeOffHeldFunds(Long balanceId, BigDecimal amount) {
        Balance balance = getBalanceById(balanceId);
        Balance savedBalance;
        if (balance.getAuthorizedBalance().compareTo(amount) >= 0) {
            balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));
            validateBalance(balance);
            savedBalance = balanceRepository.save(balance);
            log.info("Write-off held funds on balance {}. -{}", balanceId, amount);
            saveBalanceAudit(savedBalance);
        } else {
            throw new InsufficientFundsException("Insufficient held funds for writing off!");
        }
        return balanceMapper.toBalanceResponseDto(savedBalance);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.backoff.delay}",
                    multiplierExpression = "${retry.backoff.multiplier}"))
    @Transactional
    public boolean hasSufficientFunds(Long balanceId, BigDecimal amount) {
        Balance balance = getBalanceById(balanceId);
        return balance.getActualBalance().compareTo(amount) >= 0;
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "${retry.backoff.delay}",
                    multiplierExpression = "${retry.backoff.multiplier}"))
    @Transactional
    public BalanceResponseDto resetBalance(Long balanceId) {
        Balance balance = getBalanceById(balanceId);
        BigDecimal totalBalance = balance.getActualBalance().add(balance.getAuthorizedBalance());
        balance.setActualBalance(totalBalance);
        balance.setAuthorizedBalance(BigDecimal.ZERO);
        Balance savedBalance = balanceRepository.save(balance);
        saveBalanceAudit(savedBalance);
        log.info("Resetting balance {}", balanceId);
        return balanceMapper.toBalanceResponseDto(savedBalance);
    }

    @Recover
    public BalanceResponseDto recoverUpdatingBalance(OptimisticLockException e) {
        log.error("A version conflict occurred while updating a record: {}", e.getMessage());
        throw new RuntimeException("A version conflict occurred while updating a record: {}", e);
    }

    private BalanceResponseDto updateBalanceById(Long balanceId, BigDecimal authorizedBalance, BigDecimal actualBalance) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Balance with id = " + balanceId + " is not found"));
        if (authorizedBalance != null) {
            balance.setAuthorizedBalance(authorizedBalance);
        }
        if (actualBalance != null) {
            balance.setActualBalance(actualBalance);
        }
        validateBalance(balance);

        BalanceResponseDto balanceResponseDto = balanceMapper.toBalanceResponseDto(balanceRepository.save(balance));
        saveBalanceAudit(balance);
        return balanceResponseDto;
    }

    private void validateBalance(Balance balance) {
        if (balance.getAuthorizedBalance().compareTo(BigDecimal.ZERO) < 0 ||
                balance.getActualBalance().compareTo(BigDecimal.ZERO) < 0) {
            log.error("Balance cannot be negative! Current authorized balance = {}, actual balance = {}",
                    balance.getAuthorizedBalance(), balance.getActualBalance());
            throw new InsufficientFundsException("Balance cannot be negative!");
        }
        if (balance.getAccount() == null) {
            log.error("Account of balance cannot be null: {}", balance);
            throw new IllegalArgumentException("Account of balance cannot be null");
        }
    }

    private Balance getBalanceById(Long balanceId) {
        return balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Balance with id = " + balanceId + " is not found"));
    }

    private BalanceAudit saveBalanceAudit(Balance balance) {
        return balanceAuditRepository.save(balanceAuditMapper.toBalanceAuditFromBalance(balance));
    }
}
