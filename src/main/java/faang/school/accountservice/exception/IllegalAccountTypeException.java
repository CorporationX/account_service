package faang.school.accountservice.exception;

public class IllegalAccountTypeException extends RuntimeException {
    public IllegalAccountTypeException(String message) {
        super(message);
    }
}
