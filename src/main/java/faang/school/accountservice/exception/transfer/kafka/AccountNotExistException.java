package faang.school.accountservice.exception.transfer.kafka;

public class AccountNotExistException extends RuntimeException {
    public AccountNotExistException(String message) {
        super(message);
    }
}