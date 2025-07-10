package faang.school.accountservice.service;

import faang.school.accountservice.config.RetryProperties;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.BalanceAlreadyExistsException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper mapper;
    private final AccountRepository accountRepository;
    private final RetryProperties retryProperties;

    @Override
    public BalanceDto getBalanceById(Long balanceId) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Balance with id %d not found", balanceId)));

        return mapper.toDto(balance);
    }

    @Transactional
    @Override
    public void createBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Failed to create balance for account with id {}: Account not found", accountId);
                    return new EntityNotFoundException(String.format("Account with id %d not found", accountId));
                });

        log.info("Account with id {} found. Starting to create balance", accountId);
        if (account.getBalance() != null) {
            log.error("Balance for Account with id {} already exists", accountId);
            throw new BalanceAlreadyExistsException(String.format("Balance for Account with id %d already exists",
                    accountId));
        }

        Balance balance = new Balance();
        balance.setAccount(account);
        try {
            balanceRepository.save(balance);
            log.info("Balance with id {} successfully created and saved", balance.getId());
        } catch (DataIntegrityViolationException e) {
            throw new BalanceAlreadyExistsException(String.format(
                    "Balance for Account with id %d already exists. Constraint violation: %s", accountId,
                    e.getMessage()));
        }
    }

    @Transactional
    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class,
            maxAttemptsExpression = "#{@retryProperties.maxAttempts}",
            backoff = @Backoff(delayExpression = "#{@retryProperties.delay}"))
    public BalanceDto updateBalance(BalanceDto balanceDto) {
        Balance balance = balanceRepository.findById(balanceDto.id())
                .orElseThrow(() -> {
                    log.error("Balance not found for id: {}", balanceDto.id());
                    return new EntityNotFoundException("Balance not found");
                });

        balance.setAuthorizationBalance(balanceDto.authorizationBalance());
        balance.setActualBalance(balanceDto.actualBalance());
        balanceRepository.save(balance);
        log.info("Balance with id {} successfully updated", balance.getId());

        return mapper.toDto(balance);

    }

    @Recover
    public BalanceDto recover(OptimisticLockingFailureException ex, BalanceDto balanceDto) {
        log.error("Failed to update balance after multiple retries for id: {}", balanceDto.id());
        throw new RuntimeException("The balance has been updated by another thread.", ex);
    }
}