package faang.school.accountservice.exception;

public class AccountNumberGenerationException extends RuntimeException {
    public AccountNumberGenerationException(String message) {
        super(message);
    }

    public AccountNumberGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
