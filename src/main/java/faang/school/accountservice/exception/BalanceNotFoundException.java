package faang.school.accountservice.exception;

import java.util.UUID;

public class BalanceNotFoundException extends RuntimeException {
    public BalanceNotFoundException(UUID accountId) {
        super("Balance for account " + accountId + "not found");
    }
}
