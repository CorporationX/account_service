package faang.school.accountservice.exception.operation.payment;

public class AccountCurrencyMismatchException extends PaymentAuthorizationException {
    public AccountCurrencyMismatchException(String msg) {
        super(msg);
    }
}
