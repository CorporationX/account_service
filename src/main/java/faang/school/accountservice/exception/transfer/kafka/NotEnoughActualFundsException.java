package faang.school.accountservice.exception.transfer.kafka;

public class NotEnoughActualFundsException extends RuntimeException {
    public NotEnoughActualFundsException(String message) {
        super(message);
    }
}