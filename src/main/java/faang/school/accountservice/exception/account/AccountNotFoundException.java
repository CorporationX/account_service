package faang.school.accountservice.exception.account;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class AccountNotFoundException extends EntityNotFoundException {
    public AccountNotFoundException(UUID accountId) {
        super(String.format("There is no account with id %s", accountId));
    }
}
