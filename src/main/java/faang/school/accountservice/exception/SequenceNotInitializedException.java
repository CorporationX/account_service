package faang.school.accountservice.exception;

public class SequenceNotInitializedException extends IllegalStateException {
    public SequenceNotInitializedException(String message) {
        super(message);
    }

    public SequenceNotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }
}
