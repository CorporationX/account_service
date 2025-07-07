package faang.school.accountservice.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super(String.format("Account not found with id: %d", id));
    }

    public AccountNotFoundException(Long ownerId, String ownerType) {
        super(String.format("Account not found for owner with id: %d and type: %s", ownerId, ownerType));
    }
}
