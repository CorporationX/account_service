package faang.school.accountservice.exception;

public class InvalidAccountOperationException extends RuntimeException {
    public InvalidAccountOperationException(String message) {
        super(message);
    }
}
