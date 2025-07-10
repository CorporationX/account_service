package faang.school.accountservice.exception.transfer.kafka;

public class TransferFinishedException extends RuntimeException {
    public TransferFinishedException(String message) {
        super(message);
    }
}