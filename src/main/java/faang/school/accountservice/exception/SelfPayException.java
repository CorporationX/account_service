package faang.school.accountservice.exception;

public class SelfPayException extends RuntimeException {
    public SelfPayException(String message, Object... args) {
        super(String.format(message, args));
    }
}
