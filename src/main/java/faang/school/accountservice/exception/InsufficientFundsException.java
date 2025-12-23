package faang.school.accountservice.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientFundsException(String message) {
        this(message, null);
    }
}