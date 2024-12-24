package faang.school.accountservice.exception;

public class BalanceLimitException extends RuntimeException {
    public BalanceLimitException(String message) {
        super(message);
    }
}
