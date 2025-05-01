package faang.school.accountservice.validation;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountAlreadyClosedException;
import faang.school.accountservice.exception.AccountOperationConflictException;
import faang.school.accountservice.model.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AccountValidator {

    public void validateStatus(Account account, AccountStatus newStatus) {
        if (account.getAccountStatus() == newStatus) {
            log.error("Account is already in {} state for account ID: {}", newStatus, account.getId());
            throw new AccountOperationConflictException("Account is already in the requested state");
        }
        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            log.error("Attempt to change status of a closed account with ID: {}", account.getId());
            throw new AccountAlreadyClosedException("Cannot change status of a closed account");
        }
        if (account.getAccountStatus() == AccountStatus.BLOCKED) {
            log.error("Attempt to change status of a blocked account with ID: {}", account.getId());
            throw new AccountOperationConflictException("Account is already blocked");
        }
    }

    public void validateUnblock(Account account) {
        if (account.getAccountStatus() != AccountStatus.BLOCKED) {
            log.error("Account ID {} is not blocked, cannot unblock", account.getId());
            throw new AccountOperationConflictException("Account is not blocked, cannot unblock");
        }
    }
}