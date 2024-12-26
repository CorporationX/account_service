package faang.school.accountservice.exception;

public class IllegalAccountCurrencyException extends RuntimeException {
    public IllegalAccountCurrencyException(String message) {
        super(message);
    }
}
