package faang.school.accountservice.exception;

public class NotEnoughMoneyAuthorizationException extends RuntimeException {
    public NotEnoughMoneyAuthorizationException(String message) {
        super(message);
    }
}
