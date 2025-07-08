package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public BalanceDto getBalanceById(Long balanceId) {
        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Balance not found"));

        return mapper.toDto(balance);
    }

    @Transactional
    @Override
    public void createBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Failed to create balance for account with id {}: Account not found", accountId);
                    return new EntityNotFoundException("Account not found");
                });

        log.info("Account with id {} found. Starting to create balance", accountId);
        Balance balance = new Balance();
        balance.setAccount(account);
        balanceRepository.save(balance);
        log.info("Balance with id {} successfully created and saved", balance.getId());
    }

    @Transactional
    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public BalanceDto updateBalance(BalanceDto balanceDto) {
        try {
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

        } catch (OptimisticLockingFailureException ex) {
            log.warn("Optimistic Locking Failure for balance id: {}. Retrying...", balanceDto.id());
            throw ex;
        }
    }

    @Recover
    public BalanceDto recover(OptimisticLockingFailureException ex, BalanceDto balanceDto) {
        log.error("Failed to update balance after multiple retries for id: {}", balanceDto.id());
        throw new RuntimeException("The balance has been updated by another thread.", ex);
    }
}