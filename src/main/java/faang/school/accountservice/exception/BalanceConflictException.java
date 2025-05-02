package faang.school.accountservice.exception;

public class BalanceConflictException extends RuntimeException {
    public BalanceConflictException(String message) {
        super(message);
    }
}
