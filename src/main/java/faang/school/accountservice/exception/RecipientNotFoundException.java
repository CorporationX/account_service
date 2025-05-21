package faang.school.accountservice.exception;

public class RecipientNotFoundException extends RuntimeException {
    public RecipientNotFoundException(String message) {
        super(message);
    }
}
