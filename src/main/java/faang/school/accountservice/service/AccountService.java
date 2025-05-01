package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountViewDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountOperationConflictException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validation.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator accountValidator;

    @Transactional(readOnly = true)
    public AccountViewDto getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with id: {}", id);
                    return new AccountNotFoundException(String.format("Account not found with id: %d", id));
                });
        return accountMapper.toViewDto(account);
    }

    @Transactional(readOnly = true)
    public List<AccountViewDto> getAccountsByOwner(OwnerType ownerType, Long ownerId) {
        List<Account> accounts = accountRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId);
        if (accounts.isEmpty()) {
            log.info("No accounts found for owner type {} and ID {}", ownerType, ownerId);
            return List.of();
        }
        return accounts.stream()
                .map(accountMapper::toViewDto)
                .toList();
    }

    @Transactional
    public AccountViewDto openAccount(AccountCreateDto createDto) {
        String accountNumber = generateAccountNumber();

        Account account = accountMapper.toEntity(createDto);
        account.setAccountNumber(accountNumber);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setVersion(0);

        try {
            Account savedAccount = accountRepository.save(account);
            log.info("Account created with ID: {}", savedAccount.getId());
            return accountMapper.toViewDto(savedAccount);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while creating account", e);
            throw new AccountOperationConflictException("Failed to create account due to concurrent modification");
        }
    }

    @Transactional
    public AccountViewDto blockAccount(Long accountId) {
        return updateAccountStatus(accountId, AccountStatus.BLOCKED, null);
    }

    @Transactional
    public AccountViewDto closeAccount(Long accountId) {
        return updateAccountStatus(accountId, AccountStatus.CLOSED, Instant.now());
    }

    @Transactional
    public AccountViewDto unblockAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found with id: {}", accountId);
                    return new AccountNotFoundException(String.format("Account not found with id: %d", accountId));
                });

        accountValidator.validateUnblock(account);

        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setClosedAt(null);

        try {
            Account updatedAccount = accountRepository.save(account);
            log.info("Account ID {} unblocked", accountId);
            return accountMapper.toViewDto(updatedAccount);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while unblocking account ID: {}", accountId, e);
            throw new AccountOperationConflictException("Account was modified by another transaction. Please retry.");
        }
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = String.format(
                    "%016d",
                    ThreadLocalRandom.current().nextLong(1_000_000_000_000_000L, 9_999_999_999_999_999L)
            );
        } while (accountRepository.existsByAccountNumber(number));

        return number;
    }

    private AccountViewDto updateAccountStatus(Long accountId, AccountStatus newStatus, Instant closedAt) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found with id: {}", accountId);
                    return new AccountNotFoundException(String.format("Account not found with id: %d", accountId));
                });

        accountValidator.validateStatus(account, newStatus);

        account.setAccountStatus(newStatus);
        if (closedAt != null) {
            account.setClosedAt(closedAt);
        }

        try {
            Account updatedAccount = accountRepository.save(account);
            log.info("Account status updated to {} for account ID: {}", newStatus, accountId);
            return accountMapper.toViewDto(updatedAccount);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure while updating account status for account ID: {}", accountId, e);
            throw new AccountOperationConflictException("Account was modified by another transaction. Please retry.");
        }
    }
}