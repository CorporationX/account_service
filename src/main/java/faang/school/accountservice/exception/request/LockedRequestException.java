package faang.school.accountservice.exception.request;

public class LockedRequestException extends RuntimeException {
    public LockedRequestException(String message) {
        super(message);
    }
}
