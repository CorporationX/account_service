package faang.school.accountservice.exception;

public class SavingsAccountNotFoundException extends RuntimeException {
    public SavingsAccountNotFoundException(String message) {
        super(message);
    }
}
