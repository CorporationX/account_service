package faang.school.accountservice.exception;

public class SavingsAccountDuplicateException extends RuntimeException {

    public SavingsAccountDuplicateException(String message, Object... args) {
        super(String.format(message, args));
    }
}
