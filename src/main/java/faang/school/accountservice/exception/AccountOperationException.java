package faang.school.accountservice.exception;

public class AccountOperationException extends RuntimeException {
    public AccountOperationException(String message) {
        super(message);
    }
}