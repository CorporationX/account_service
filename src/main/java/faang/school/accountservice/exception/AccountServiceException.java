package faang.school.accountservice.exception;

public class AccountServiceException extends RuntimeException {
    public AccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}