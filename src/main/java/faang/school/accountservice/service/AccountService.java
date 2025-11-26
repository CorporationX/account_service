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
import faang.school.accountservice.validator.AccountValidator;
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
    private final AccountValidator accountValidator;

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long accountId) {
        log.info("Getting account with id: {}", accountId);
        Account account = findAccountById(accountId);
        return accountMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber) {
        log.info("Getting account with number: {}", accountNumber);
        accountValidator.validateAccountNumber(accountNumber);

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

        accountValidator.validateAccountOpening(
                request.ownerId(),
                request.ownerType(),
                request.currency()
        );

        // Генерация уникального номера счета с проверкой в БД
        String accountNumber = accountNumberGenerator.generate();
        log.debug("Generated unique account number: {}", accountNumber);

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

        accountValidator.validateBlockAccount(account);

        account.setStatus(AccountStatus.BLOCKED);

        Account updatedAccount = saveWithOptimisticLock(account, "blocking");
        log.info("Successfully blocked account: {}", accountId);
        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse freezeAccount(Long accountId) {
        log.info("Freezing account with id: {}", accountId);

        Account account = findAccountById(accountId);

        accountValidator.validateFreezeAccount(account);

        account.setStatus(AccountStatus.FROZEN);

        Account updatedAccount = saveWithOptimisticLock(account, "freezing");
        log.info("Successfully frozen account: {}", accountId);
        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse unfreezeAccount(Long accountId) {
        log.info("Unfreezing account with id: {}", accountId);

        Account account = findAccountById(accountId);

        accountValidator.validateUnfreezeAccount(account);

        account.setStatus(AccountStatus.ACTIVE);

        Account updatedAccount = saveWithOptimisticLock(account, "unfreezing");
        log.info("Successfully unfrozen account: {}", accountId);
        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse closeAccount(Long accountId) {
        log.info("Closing account with id: {}", accountId);

        Account account = findAccountById(accountId);

        accountValidator.validateCloseAccount(account);

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());

        Account updatedAccount = saveWithOptimisticLock(account, "closing");
        log.info("Successfully closed account: {}", accountId);
        return accountMapper.toResponse(updatedAccount);
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

    private Account saveWithOptimisticLock(Account account, String operation) {
        try {
            return accountRepository.save(account);
        } catch (OptimisticLockingFailureException | OptimisticLockException e) {
            log.error("Optimistic lock error while {} account: {}", operation, account.getId());
            throw new InvalidAccountOperationException(
                    String.format("Account %d was modified by another process. " +
                            "Please refresh and try again.", account.getId()));
        }
    }
}