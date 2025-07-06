package faang.school.accountservice.exception.balance;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class BalanceNotFoundException extends EntityNotFoundException {
    public BalanceNotFoundException(UUID balanceId) {
        super(String.format("Balance with %s not found", balanceId.toString()));
    }
}
