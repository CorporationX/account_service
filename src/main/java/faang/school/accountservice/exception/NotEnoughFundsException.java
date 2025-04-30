package faang.school.accountservice.exception;

public class NotEnoughFundsException extends RuntimeException {
    public NotEnoughFundsException(String message, Object... args) {
        super(String.format(message, args));
    }

}
