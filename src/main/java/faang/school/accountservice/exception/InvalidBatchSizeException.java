package faang.school.accountservice.exception;

public class InvalidBatchSizeException extends RuntimeException {
    public InvalidBatchSizeException(String message) {
        super(message);
    }
}
