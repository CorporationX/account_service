package faang.school.accountservice.service;


import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.dto.OpenAccountRequest;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.InvalidAccountOperationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.util.AccountNumberGenerator;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGenerator accountNumberGenerator;

    private static final int MAX_ACCOUNTS_PER_OWNER = 10;

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long accountId) {
        log.info("Getting account with id: {}", accountId);
        Account account = findAccountById(accountId);
        return accountMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber) {
        log.info("Getting account with number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with number: " + accountNumber));
        return accountMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getOwnerAccounts(Long ownerId, OwnerType ownerType) {
        log.info("Getting accounts for owner: {} of type: {}", ownerId, ownerType);
        List<Account> accounts = accountRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
        return accounts.stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Transactional
    public AccountResponse openAccount(OpenAccountRequest request) {
        log.info("Opening new account for owner: {} of type: {}",
                request.ownerId(), request.ownerType());

        validateAccountLimit(request.ownerId(), request.ownerType());

        String accountNumber = accountNumberGenerator.generate();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .ownerId(request.ownerId())
                .ownerType(request.ownerType())
                .accountType(request.accountType())
                .currency(request.currency())
                .status(AccountStatus.ACTIVE)
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Successfully opened account: {} for owner: {}",
                savedAccount.getAccountNumber(), request.ownerId());

        return accountMapper.toResponse(savedAccount);
    }

    @Transactional
    public AccountResponse blockAccount(Long accountId, String reason) {
        log.info("Blocking account with id: {}. Reason: {}", accountId, reason);

        Account account = findAccountById(accountId);

        if (account.isClosed()) {
            throw new InvalidAccountOperationException(
                    "Cannot block a closed account: " + accountId);
        }

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new InvalidAccountOperationException(
                    "Account is already blocked: " + accountId);
        }

        account.setStatus(AccountStatus.BLOCKED);

        try {
            Account updatedAccount = accountRepository.save(account);
            log.info("Successfully blocked account: {}", accountId);
            return accountMapper.toResponse(updatedAccount);
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            log.error("Optimistic lock error while blocking account: {}", accountId);
            throw new InvalidAccountOperationException(
                    "Account was modified by another process. Please try again.");
        }
    }

    @Transactional
    public AccountResponse freezeAccount(Long accountId) {
        log.info("Freezing account with id: {}", accountId);

        Account account = findAccountById(accountId);

        if (account.isClosed()) {
            throw new InvalidAccountOperationException(
                    "Cannot freeze a closed account: " + accountId);
        }

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new InvalidAccountOperationException(
                    "Account is already frozen: " + accountId);
        }

        account.setStatus(AccountStatus.FROZEN);

        try {
            Account updatedAccount = accountRepository.save(account);
            log.info("Successfully frozen account: {}", accountId);
            return accountMapper.toResponse(updatedAccount);
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            log.error("Optimistic lock error while freezing account: {}", accountId);
            throw new InvalidAccountOperationException(
                    "Account was modified by another process. Please try again.");
        }
    }

    @Transactional
    public AccountResponse unfreezeAccount(Long accountId) {
        log.info("Unfreezing account with id: {}", accountId);

        Account account = findAccountById(accountId);

        if (!account.isFrozen()) {
            throw new InvalidAccountOperationException(
                    "Account is not frozen: " + accountId);
        }

        account.setStatus(AccountStatus.ACTIVE);

        try {
            Account updatedAccount = accountRepository.save(account);
            log.info("Successfully unfrozen account: {}", accountId);
            return accountMapper.toResponse(updatedAccount);
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            log.error("Optimistic lock error while unfreezing account: {}", accountId);
            throw new InvalidAccountOperationException(
                    "Account was modified by another process. Please try again.");
        }
    }

    @Transactional
    public AccountResponse closeAccount(Long accountId) {
        log.info("Closing account with id: {}", accountId);

        Account account = findAccountById(accountId);

        if (account.isClosed()) {
            throw new InvalidAccountOperationException(
                    "Account is already closed: " + accountId);
        }

        // Проверка, что баланс нулевой
        if (account.getBalance().compareTo(java.math.BigDecimal.ZERO) != 0) {
            throw new InvalidAccountOperationException(
                    "Cannot close account with non-zero balance. Current balance: "
                            + account.getBalance());
        }

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());

        try {
            Account updatedAccount = accountRepository.save(account);
            log.info("Successfully closed account: {}", accountId);
            return accountMapper.toResponse(updatedAccount);
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            log.error("Optimistic lock error while closing account: {}", accountId);
            throw new InvalidAccountOperationException(
                    "Account was modified by another process. Please try again.");
        }
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getActiveAccountsByCurrency(
            Long ownerId,
            OwnerType ownerType,
            Currency currency) {
        log.info("Getting active accounts for owner: {} with currency: {}", ownerId, currency);

        List<Account> accounts = accountRepository.findByOwnerIdAndOwnerTypeAndCurrency(
                ownerId, ownerType, currency);

        return accounts.stream()
                .filter(Account::isActive)
                .map(accountMapper::toResponse)
                .toList();
    }

    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));
    }

    private void validateAccountLimit(Long ownerId, OwnerType ownerType) {
        long accountCount = accountRepository.countActiveAccountsByOwner(ownerId, ownerType);
        if (accountCount >= MAX_ACCOUNTS_PER_OWNER) {
            throw new InvalidAccountOperationException(
                    String.format("Owner %d has reached the maximum limit of %d active accounts",
                            ownerId, MAX_ACCOUNTS_PER_OWNER));
        }
    }
}
