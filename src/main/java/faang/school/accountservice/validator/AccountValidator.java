package faang.school.accountservice.validator;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.InvalidAccountOperationException;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountValidator {

    private final AccountRepository accountRepository;

    private static final int MAX_ACCOUNTS_PER_OWNER = 10;
    private static final int MIN_ACCOUNT_NUMBER_LENGTH = 12;
    private static final int MAX_ACCOUNT_NUMBER_LENGTH = 20;

    public void validateAccountOpening(Long ownerId, OwnerType ownerType, Currency currency) {
        log.debug("Validating account opening for owner: {} of type: {}", ownerId, ownerType);

        // Проверка лимита счетов на владельца
        validateAccountLimit(ownerId, ownerType);

        // Проверка: можно ли открыть еще один счет в этой валюте
        validateCurrencyLimit(ownerId, ownerType, currency);
    }

    public void validateBlockAccount(Account account) {
        log.debug("Validating block operation for account: {}", account.getId());

        if (account.isClosed()) {
            throw new InvalidAccountOperationException(
                    String.format("Cannot block a closed account: %d", account.getId()));
        }

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new InvalidAccountOperationException(
                    String.format("Account is already blocked: %d", account.getId()));
        }
    }

    public void validateFreezeAccount(Account account) {
        log.debug("Validating freeze operation for account: {}", account.getId());

        if (account.isClosed()) {
            throw new InvalidAccountOperationException(
                    String.format("Cannot freeze a closed account: %d", account.getId()));
        }

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new InvalidAccountOperationException(
                    String.format("Account is already frozen: %d", account.getId()));
        }
    }

    public void validateUnfreezeAccount(Account account) {
        log.debug("Validating unfreeze operation for account: {}", account.getId());

        if (!account.isFrozen()) {
            throw new InvalidAccountOperationException(
                    String.format("Account is not frozen and cannot be unfrozen: %d",
                            account.getId()));
        }
    }

    public void validateCloseAccount(Account account) {
        log.debug("Validating close operation for account: {}", account.getId());

        if (account.isClosed()) {
            throw new InvalidAccountOperationException(
                    String.format("Account is already closed: %d", account.getId()));
        }

        if (account.getBalance().getActualBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidAccountOperationException(
                    String.format("Cannot close account with non-zero balance. " +
                                    "Account: %d, Current balance: %s",
                            account.getId(), account.getBalance()));
        }
    }

    public void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new InvalidAccountOperationException("Account number cannot be empty");
        }

        if (!accountNumber.matches("^\\d{" + MIN_ACCOUNT_NUMBER_LENGTH + ","
                + MAX_ACCOUNT_NUMBER_LENGTH + "}$")) {
            throw new InvalidAccountOperationException(
                    String.format("Account number must contain %d to %d digits only",
                            MIN_ACCOUNT_NUMBER_LENGTH, MAX_ACCOUNT_NUMBER_LENGTH));
        }
    }

    private void validateAccountLimit(Long ownerId, OwnerType ownerType) {
        long accountCount = accountRepository.countActiveAccountsByOwner(ownerId, ownerType);

        if (accountCount >= MAX_ACCOUNTS_PER_OWNER) {
            log.warn("Owner {} has reached account limit: {}/{}",
                    ownerId, accountCount, MAX_ACCOUNTS_PER_OWNER);
            throw new InvalidAccountOperationException(
                    String.format("Owner %d has reached the maximum limit of %d active accounts. " +
                                    "Please close unused accounts before opening a new one.",
                            ownerId, MAX_ACCOUNTS_PER_OWNER));
        }
    }

    private void validateCurrencyLimit(Long ownerId, OwnerType ownerType, Currency currency) {
        boolean exists = accountRepository.existsByOwnerIdAndOwnerTypeAndCurrencyAndStatus(
                ownerId, ownerType, currency, AccountStatus.ACTIVE);

        if (exists) {
            log.info("Owner {} already has an active account in currency {}",
                    ownerId, currency);
            throw new InvalidAccountOperationException(
                String.format("Owner %d already has an active account in currency %s",
                    ownerId, currency));
        }
    }
}