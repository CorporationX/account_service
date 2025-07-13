package faang.school.accountservice.exception.operation.payment;

public class PaymentAuthorizationException extends RuntimeException {
    public PaymentAuthorizationException(String msg) {
        super(msg);
    }
}
