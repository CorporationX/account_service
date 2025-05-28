package faang.school.accountservice.exception;

public class ConcurrentModificationException extends RuntimeException {

    public ConcurrentModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
