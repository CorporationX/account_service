package faang.school.accountservice.exception;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Long id) {
        super("Request not found: %d".formatted(id));
    }
}
