package faang.school.accountservice.exception;

public class AccountGenerationException extends RuntimeException {
    public AccountGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
