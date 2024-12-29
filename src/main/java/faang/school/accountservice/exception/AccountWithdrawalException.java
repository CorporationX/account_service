package faang.school.accountservice.exception;

public class AccountWithdrawalException extends RuntimeException {
    public AccountWithdrawalException(String message) {
        super(message);
    }
}
