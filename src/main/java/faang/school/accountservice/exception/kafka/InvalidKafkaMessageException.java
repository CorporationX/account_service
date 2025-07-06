package faang.school.accountservice.exception.kafka;

public class InvalidKafkaMessageException extends RuntimeException {
    public InvalidKafkaMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
