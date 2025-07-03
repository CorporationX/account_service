package faang.school.accountservice.exception.balance;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class AuthorizationBalanceNotFoundException extends EntityNotFoundException {
    public AuthorizationBalanceNotFoundException(UUID authBalanceId) {
        super(String.format("Authorization balance with %s not found", authBalanceId.toString()));
    }
}
