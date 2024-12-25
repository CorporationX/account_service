package faang.school.accountservice.exception.account;

public class AccountNotValidException extends RuntimeException {
    public AccountNotValidException(String message) {
        super(message);
    }
}
