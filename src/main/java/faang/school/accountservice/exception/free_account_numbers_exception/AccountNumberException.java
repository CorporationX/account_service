package faang.school.accountservice.exception.free_account_numbers_exception;

public class AccountNumberException extends RuntimeException {
    public AccountNumberException(String message) {
        super(message);
    }

    public AccountNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}
