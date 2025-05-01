package faang.school.accountservice.exception;

public class AccountOperationConflictException extends RuntimeException {
    public AccountOperationConflictException(String message) {
        super(message);
    }
}