package faang.school.accountservice.exception;

public class WrongAmountException extends RuntimeException {
    public WrongAmountException(String message, Object... args) {
        super(String.format(message, args));
    }

}
