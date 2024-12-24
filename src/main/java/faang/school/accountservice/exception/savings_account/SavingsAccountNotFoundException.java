package faang.school.accountservice.exception.savings_account;

public class SavingsAccountNotFoundException extends RuntimeException {
    public SavingsAccountNotFoundException(String message) {
        super(message);
    }
}