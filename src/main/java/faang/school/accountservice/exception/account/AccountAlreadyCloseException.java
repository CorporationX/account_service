package faang.school.accountservice.exception.account;

import java.util.UUID;

public class AccountAlreadyCloseException extends RuntimeException {
    public AccountAlreadyCloseException(UUID id) {
        super(String.format("Account with id %s already close", id.toString()));
    }
}
