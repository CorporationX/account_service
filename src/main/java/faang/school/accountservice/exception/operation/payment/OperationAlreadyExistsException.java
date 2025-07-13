package faang.school.accountservice.exception.operation.payment;

public class OperationAlreadyExistsException extends RuntimeException {
    public OperationAlreadyExistsException(String msg) {
        super(msg);
    }
}
