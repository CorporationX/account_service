package faang.school.accountservice.exception.transfer.kafka;

public class AccountNotAvailableException extends RuntimeException {
    public AccountNotAvailableException(String message) {
        super(message);
    }
}