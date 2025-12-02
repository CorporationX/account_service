package faang.school.accountservice.service;

import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.ForbiddenException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class OperationValidator {

    private final AccountRepository accountRepository;

    public Account validateAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.warn("Account with number {} not found", accountNumber);
                    return new EntityNotFoundException(
                            String.format("Account with number %s not found", accountNumber));
                });
    }

    public Account validateAccountActive(String accountNumber) {
        Account account = validateAccount(accountNumber);
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            log.error("Account with number {} is not active", accountNumber);
            throw new ForbiddenException(String.format("Account with number %s is not active", accountNumber));
        }
        return account;
    }

    public Account validateAccountFrozen(String accountNumber) {
        Account account = validateAccount(accountNumber);
        if (account.getAccountStatus() != AccountStatus.FROZEN) {
            log.error("Account with number {} is not frozen", accountNumber);
            throw new ForbiddenException(String.format("Account with number %s is not frozen", accountNumber));
        }
        return account;
    }

    public Account validateAccountReadyToClose(String accountNumber) {
        Account account = validateAccount(accountNumber);
        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            log.error("Account with number {} is already closed", accountNumber);
            throw new ForbiddenException(String.format("Account with number %s is already closed", accountNumber));
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            log.error("Account with number {} has a non-zero balance", accountNumber);
            throw new ForbiddenException(String.format("Account with number %s has a non-zero balance", accountNumber));
        }
        return account;
    }
}