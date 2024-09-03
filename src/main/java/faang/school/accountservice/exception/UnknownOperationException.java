package faang.school.accountservice.exception;

public class UnknownOperationException extends RuntimeException {
    public UnknownOperationException(String message) {
        super(message);
    }
}
