package faang.school.accountservice.exception.operation.payment;

public class UnauthorizedAccountAccessException extends PaymentAuthorizationException {
    public UnauthorizedAccountAccessException(String msg) {
        super(msg);
    }
}
