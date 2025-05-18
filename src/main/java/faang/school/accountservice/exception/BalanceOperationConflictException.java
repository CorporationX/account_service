package faang.school.accountservice.exception;

public class BalanceOperationConflictException extends RuntimeException{
    public BalanceOperationConflictException(String message, Exception exception) {
        super(message, exception);
    }
}
