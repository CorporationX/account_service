package faang.school.accountservice.exception.transfer.kafka;

public class WrongStageException extends RuntimeException {
    public WrongStageException(String message) {
        super(message);
    }
}