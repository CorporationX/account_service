package faang.school.accountservice.exception;

import java.util.UUID;

public class BalanceAlreadyExistsException extends RuntimeException {
    public BalanceAlreadyExistsException(UUID accountId) {
        super("Balance already exists for account: " + accountId);
    }
}
