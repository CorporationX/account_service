package faang.school.accountservice.exception;

public class BalanceAlreadyExistsException extends RuntimeException {
    public BalanceAlreadyExistsException(String message) {
        super(message);
    }
}
