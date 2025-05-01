package faang.school.accountservice.exception;

public class AccountAlreadyClosedException extends RuntimeException {
    public AccountAlreadyClosedException(String message) {
        super(message);
    }
}