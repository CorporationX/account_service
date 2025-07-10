package faang.school.accountservice.exception.operation.payment;

public class OperationAlreadyExistsException extends PaymentAuthorizationException {
    public OperationAlreadyExistsException(String msg) {
        super(msg);
    }
}
