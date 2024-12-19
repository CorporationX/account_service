package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountOwnerRepository;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountOwnerRepository accountOwnerRepository;
    private final AccountMapper accountMapper;
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        log.info("Successfully get account with id: {}", id);
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountResponse openAccount(AccountRequest request) {
        log.info("Start opening a new account for ownerId: {}, ownerType: {}",
                request.getOwnerId(), request.getOwnerType());
        AccountOwner owner = accountOwnerRepository
                .findByOwnerIdAndOwnerType(request.getOwnerId(), request.getOwnerType())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        Account account = Account.builder()
                .accountNumber(freeAccountNumbersService.getFreeAccountNumber(request.getType()))
                .type(request.getType())
                .currency(request.getCurrency())
                .status(AccountStatus.ACTIVE)
                .owner(owner)
                .build();

        account = accountRepository.save(account);
        log.info("Successfully opened account with number: {}, for ownerId: {}",
                account.getAccountNumber(), request.getOwnerId());
        return accountMapper.toDto(account);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public AccountResponse blockAccount(Long id) {
        log.info("Blocking account with id: {}", id);
        Account account = getAccountEntity(id);

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new IllegalStateException("Account is already blocked");
        }

        account.setStatus(AccountStatus.BLOCKED);
        account = accountRepository.save(account);
        log.info("Successfully blocked account with id: {}", id);
        return accountMapper.toDto(account);
    }

    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public AccountResponse closeAccount(Long id) {
        log.info("Closing account with id: {}", id);
        Account account = getAccountEntity(id);

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalStateException("Account is already closed");
        }

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        account = accountRepository.save(account);
        log.info("Successfully closed account with id: {}", id);
        return accountMapper.toDto(account);
    }

    private Account getAccountEntity(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }
}
