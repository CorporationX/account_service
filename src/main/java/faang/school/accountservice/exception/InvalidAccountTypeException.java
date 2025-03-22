package faang.school.accountservice.exception;

public class InvalidAccountTypeException extends IllegalArgumentException {
    public InvalidAccountTypeException(String message) {
        super(message);
    }
}
