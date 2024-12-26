package faang.school.accountservice.exception;

public class IllegalAccountOwnerTypeException extends RuntimeException {
    public IllegalAccountOwnerTypeException(String message) {
        super(message);
    }
}
