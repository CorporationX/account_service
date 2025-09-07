package faang.school.accountservice.exception;

public class ClosedAccountOperationException extends RuntimeException {
    public ClosedAccountOperationException(String message) {
        super(message);
    }
}
