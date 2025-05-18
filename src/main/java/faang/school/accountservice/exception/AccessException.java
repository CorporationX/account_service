package faang.school.accountservice.exception;

public class AccessException extends RuntimeException {
    public AccessException(String message, Object... args) {
        super(String.format(message, args));
    }
}
