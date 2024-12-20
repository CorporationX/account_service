package faang.school.accountservice.exception;

public class IllegalAccountAccessException extends RuntimeException {
    public IllegalAccountAccessException(String message) {
        super(message);
    }
}
