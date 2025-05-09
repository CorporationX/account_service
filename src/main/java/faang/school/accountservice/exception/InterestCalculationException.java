package faang.school.accountservice.exception;

public class InterestCalculationException extends RuntimeException {
    public InterestCalculationException(String message) {
        super(message);
    }

    public InterestCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
