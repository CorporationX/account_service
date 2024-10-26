package faang.school.accountservice.exception;

public class ResourceNotAvailableException extends RuntimeException {
    public ResourceNotAvailableException(String message, Object... args) {
        super(String.format(message, args));
    }
}
