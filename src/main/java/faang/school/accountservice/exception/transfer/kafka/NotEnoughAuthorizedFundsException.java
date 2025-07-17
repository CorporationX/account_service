package faang.school.accountservice.exception.transfer.kafka;

public class NotEnoughAuthorizedFundsException extends RuntimeException {
    public NotEnoughAuthorizedFundsException(String message) {
        super(message);
    }
}