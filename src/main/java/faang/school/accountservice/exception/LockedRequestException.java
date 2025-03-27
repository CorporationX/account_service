package faang.school.accountservice.exception;

public class LockedRequestException extends RuntimeException {
    public LockedRequestException(String message) {
        super(message);
    }
}
