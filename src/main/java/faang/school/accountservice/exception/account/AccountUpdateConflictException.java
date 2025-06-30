package faang.school.accountservice.exception.account;

public class AccountUpdateConflictException extends RuntimeException {
    public AccountUpdateConflictException(String msg) {
        super(msg);
    }
}
