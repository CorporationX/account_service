package faang.school.accountservice.exception;

public class BalanceInvariantViolationException extends RuntimeException {
    public BalanceInvariantViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BalanceInvariantViolationException(String message) {
        this(message, null);
    }
}