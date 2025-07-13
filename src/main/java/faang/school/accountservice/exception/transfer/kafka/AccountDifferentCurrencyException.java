package faang.school.accountservice.exception.transfer.kafka;

public class AccountDifferentCurrencyException extends RuntimeException {
    public AccountDifferentCurrencyException(String message) {
        super(message);
    }
}