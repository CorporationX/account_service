package faang.school.accountservice.handler;

public class BalanceException extends RuntimeException {
    public BalanceException(String message) {
        super(message);
    }
}