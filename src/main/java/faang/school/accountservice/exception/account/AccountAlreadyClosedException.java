package faang.school.accountservice.exception.account;

import java.util.UUID;

public class AccountAlreadyClosedException extends RuntimeException {
    public AccountAlreadyClosedException(UUID accountId) {
        super(String.format("Account with id %s already closed", accountId));
    }
}
