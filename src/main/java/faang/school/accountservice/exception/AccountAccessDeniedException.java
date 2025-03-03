package faang.school.accountservice.exception;

public class AccountAccessDeniedException extends RuntimeException {

    public AccountAccessDeniedException(String message) {
        super(message);
    }
}