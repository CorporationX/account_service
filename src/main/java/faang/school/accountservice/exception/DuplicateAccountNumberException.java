package faang.school.accountservice.exception;

public class DuplicateAccountNumberException extends IllegalArgumentException {
    public DuplicateAccountNumberException(String message) {
        super(message);
    }

    public DuplicateAccountNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}
