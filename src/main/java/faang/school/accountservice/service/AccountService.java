package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.Account;
import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    public static final int RETRY_ATTEMPTS = 4;
    public static final long RETRY_DELAY = 100L;

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountDto openAccount(AccountCreateDto accountDto) {
        Account account = accountMapper.fromCreateDto(accountDto);
        account.setAccountNumber(generateAccountNumber(account));
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Transactional(readOnly = true)
    public Account getAccountById(long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found by id: " + id));
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountDtoById(long id) {
        return accountMapper.toDto(getAccountById(id));
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getAccountByUserId(long userId) {
        return accountRepository.findByUserId(userId).map(accountMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getAccountByProjectId(long projectId) {
        return accountRepository.findByProjectId(projectId).map(accountMapper::toDto).toList();
    }

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = RETRY_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, random = true)
    )
    @Transactional
    public AccountDto friezeAccount(long id) {
        log.info("Friezing retry count: {}",
                Objects.requireNonNull(
                        RetrySynchronizationManager.getContext()
                ).getRetryCount());
        Account account = getAccountById(id);
        if (account.getStatus().equals(AccountStatus.FROZEN)) {
            throw new IllegalStateException("Cannot frieze frozen account");
        }
        account.setStatus(AccountStatus.FROZEN);
        return accountMapper.toDto(account);
    }

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = RETRY_ATTEMPTS,
            backoff = @Backoff(delay = RETRY_DELAY, random = true)
    )
    @Transactional
    public AccountDto closeAccount(long id) {
        log.info("Closing retry count: {}",
                Objects.requireNonNull(
                        RetrySynchronizationManager.getContext()
                ).getRetryCount());
        Account account = getAccountById(id);
        if (account.getStatus().equals(AccountStatus.CLOSED)) {
            throw new IllegalStateException("Cannot close closed account");
        }
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        return accountMapper.toDto(account);
    }

    @Recover
    public AccountDto recoveryChange(ObjectOptimisticLockingFailureException e, long id) {
        log.info("Couldn't finish updating after max attempts, account id: {}", id);
        throw new RuntimeException("Couldn't finish updating", e);
    }

    private String generateAccountNumber(Account account) {
        // simple generating account number
        String res = account.hashCode() + account.getBank().toString();
        int length = Math.min(res.length(), 20);
        return res.substring(0, length);
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Account not found by accountNumber: " + accountNumber));
    }
}
