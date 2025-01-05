package faang.school.accountservice.exception;

public class BalanceInsufficientFundsException extends RuntimeException {
    public BalanceInsufficientFundsException(String message) {
        super(message);
    }
}
