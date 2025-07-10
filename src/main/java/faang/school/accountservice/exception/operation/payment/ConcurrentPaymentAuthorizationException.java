package faang.school.accountservice.exception.operation.payment;

public class ConcurrentPaymentAuthorizationException extends PaymentAuthorizationException {
    public ConcurrentPaymentAuthorizationException(String msg) {
        super(msg);
    }
}
