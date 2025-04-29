package faang.school.accountservice.exception.free_account_numbers_exception;

public class AccountNumberGenerationException extends AccountNumberException {
    public AccountNumberGenerationException(String message) {
        super(message);
    }

    public AccountNumberGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
