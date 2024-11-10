package faang.school.accountservice.exception;

public class NotUniqueAccountNumberException extends RuntimeException {
    public NotUniqueAccountNumberException(String message) {
        super(message);
    }
}
