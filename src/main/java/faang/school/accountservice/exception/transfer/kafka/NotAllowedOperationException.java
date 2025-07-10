package faang.school.accountservice.exception.transfer.kafka;

public class NotAllowedOperationException extends RuntimeException {
    public NotAllowedOperationException(String message) {
        super(message);
    }
}