package faang.school.accountservice.exception;

public class IllegalAccountStatusException extends RuntimeException {
    public IllegalAccountStatusException(String message) {
        super(message);
    }
}
