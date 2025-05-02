package faang.school.accountservice.exception;

public class OpenRequestExistsException extends RuntimeException {
    public OpenRequestExistsException(String message) {
        super(message);
    }
}
