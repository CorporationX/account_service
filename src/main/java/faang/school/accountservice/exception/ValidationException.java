package faang.school.accountservice.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message, Object... args) {
        super(String.format(message, args));
    }
}
