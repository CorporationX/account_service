package faang.school.accountservice.exception;

public class NoAvailableAccountNumberException extends RuntimeException {
    public NoAvailableAccountNumberException(String message) {
        super(message);
    }
}
