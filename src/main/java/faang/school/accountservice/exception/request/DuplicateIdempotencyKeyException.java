package faang.school.accountservice.exception.request;

public class DuplicateIdempotencyKeyException extends RuntimeException {
    public DuplicateIdempotencyKeyException(String message) {
        super(message);
    }
}
