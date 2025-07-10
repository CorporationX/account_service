package faang.school.accountservice.exception.operation.payment;

public class InvalidAccountOwnerException extends PaymentAuthorizationException {
    public InvalidAccountOwnerException(String msg) {
        super(msg);
    }
}
