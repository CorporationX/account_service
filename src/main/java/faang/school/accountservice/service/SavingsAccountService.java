package faang.school.accountservice.service;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.mappers.SavingsAccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final AccountRepository accountRepository;
    private final SavingsAccountMapper mapper;

    @Value("${spring.property-values.retry.max-attempts}")
    private int retryMaxAttempts;

    @Value("${spring.property-values.retry.backoff-delay}")
    private int retryBackoffDelay;

    public SavingsAccountDto getSavingsAccountById(Long savingsAccountId) {
        validateSavingsAccountId(savingsAccountId);
        log.info("Received a request to get the savings account by id: {}", savingsAccountId);
        SavingsAccount account = savingsAccountRepository.findById(savingsAccountId)
           .orElseThrow(() -> {
                    log.error("Received a request to get a savings account with invalid id {}", savingsAccountId);
                    throw new EntityNotFoundException("Savings account with id " + savingsAccountId + " not found");
        });

        return mapper.toDto(account);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttemptsExpression = "${spring.property-values.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${spring.property-values.retry.backoff-delay}")
    )
    public SavingsAccountDto createAccount(SavingsAccountDto savingsAccountDto) throws SQLException {
        validateAccount(savingsAccountDto.getId());
        SavingsAccount savingsAccount = mapper.toEntity(savingsAccountDto);
        savingsAccount.setCreatedAt(LocalDateTime.now());
        log.info("RETRY NUMBER: {} ", RetrySynchronizationManager.getContext().getRetryCount());
        SavingsAccount result = savingsAccountRepository.save(savingsAccount);
        return mapper.toDto(result);
    }

    @Recover
    public SavingsAccountDto recover(OptimisticLockingFailureException exception, SavingsAccountDto savingsAccountDto) {
        log.error("Failed to create account after {} attempts", retryMaxAttempts, exception);
        throw new RuntimeException("Failed to create account after " + retryMaxAttempts + " attempts");
    }

    private void validateAccount(Long id) {
        if (id == null) {
            log.info("Received a request to create a savings account with ID NULL");
            throw new IllegalArgumentException("ERROR: ID cannot be NULL");
        }
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> {
                log.info("A request has been made to get the account with invalid id");
                throw new AccountNotFoundException("ERROR: Account cannot be found in DB!");
        });

        if (account.getAccountType() != AccountType.SAVINGS) {
            log.info("Received a request to create a savings account with invalid type");
            throw new RuntimeException("ERROR: Account type is not SAVINGS!");
        }
    }

    private void validateSavingsAccountId(Long savingsAccountId) {
        if (savingsAccountId == null || savingsAccountId <= 0) {
            log.info("Received a request to create a savings account with ID NULL or LESS THAN 0");
            throw new IllegalArgumentException("ERROR: ID cannot be NULL or LESS THAN 0");
        }
    }
}
