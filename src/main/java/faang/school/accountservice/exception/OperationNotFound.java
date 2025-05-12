package faang.school.accountservice.exception;

public class OperationNotFound extends RuntimeException {
    public OperationNotFound(String message) {
        super(message);
    }
}
