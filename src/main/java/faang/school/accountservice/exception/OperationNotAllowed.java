package faang.school.accountservice.exception;

public class OperationNotAllowed extends RuntimeException {
    public OperationNotAllowed(String message) {
        super(message);
    }
}
