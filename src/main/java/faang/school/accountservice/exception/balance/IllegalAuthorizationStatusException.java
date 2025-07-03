package faang.school.accountservice.exception.balance;

import faang.school.accountservice.entity.balance.AuthorizationBalanceType;

import java.util.UUID;

public class IllegalAuthorizationStatusException extends RuntimeException {
    public IllegalAuthorizationStatusException(UUID id, AuthorizationBalanceType status) {
        super(String.format("AuthorizationBalance %s has illegal status %s", id.toString(), status.toString()));
    }
}
