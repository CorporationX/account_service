package faang.school.accountservice.validator.account;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.common.DataValidationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountValidator {

    public static void validateAccountNotClosed(Account account) {
        if (account.getStatus() == AccountStatus.CLOSED) {
            log.error("Account with id {} already closed", account.getId());
            throw new DataValidationException(String.format("Account with id %s already closed", account.getId()));
        }
    }
}
