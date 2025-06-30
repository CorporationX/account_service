package faang.school.accountservice.validation.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.AccountStatus;
import faang.school.accountservice.exception.account.AccountAlreadyCloseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class AccountValidator {
    public void checkCloseAccount(Account account) {
        if (Objects.equals(account.getStatus(), AccountStatus.CLOSED)) {
            log.error("Account with id {} already closed", account.getId());
            throw new AccountAlreadyCloseException(account.getId());
        }
    }
}
