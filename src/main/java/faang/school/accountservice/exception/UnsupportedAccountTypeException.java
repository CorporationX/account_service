package faang.school.accountservice.exception;

public class UnsupportedAccountTypeException extends RuntimeException {
    public UnsupportedAccountTypeException(String message) {
        super(message);
    }
}
