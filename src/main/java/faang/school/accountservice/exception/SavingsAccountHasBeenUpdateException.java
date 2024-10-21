package faang.school.accountservice.exception;

public class SavingsAccountHasBeenUpdateException extends RuntimeException {
    private static final String MESSAGE = "Account id=%s has been update. Reload information";

    public SavingsAccountHasBeenUpdateException(Long id) {
        super(String.format(MESSAGE, id));
    }
}
