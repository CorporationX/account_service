package faang.school.accountservice.exception;

public class InvalidBalanceOperationException extends RuntimeException {
    public InvalidBalanceOperationException(String message) {
        super(message);
    }
}
