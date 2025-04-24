package faang.school.accountservice.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
