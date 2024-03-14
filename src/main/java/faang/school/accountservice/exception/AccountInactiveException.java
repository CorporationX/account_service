package faang.school.accountservice.exception;

public class AccountInactiveException extends RuntimeException{
    public AccountInactiveException(String message) {
        super(message);
    }
}
