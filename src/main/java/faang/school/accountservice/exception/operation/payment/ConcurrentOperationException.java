package faang.school.accountservice.exception.operation.payment;

public class ConcurrentOperationException extends RuntimeException {
    public ConcurrentOperationException(String msg) {
        super(msg);
    }
}
