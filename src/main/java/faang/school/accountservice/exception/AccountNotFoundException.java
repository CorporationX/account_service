package faang.school.accountservice.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("Account with id=" + id + " not found.");
    }
}