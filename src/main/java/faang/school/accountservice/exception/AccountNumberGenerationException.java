package faang.school.accountservice.exception;

import faang.school.accountservice.enums.AccountType;

public class AccountNumberGenerationException extends RuntimeException {

    private final AccountType accountType;

    public AccountNumberGenerationException(String message, AccountType accountType) {
        super(message);
        this.accountType = accountType;
    }

    public AccountNumberGenerationException(String message, Throwable cause, AccountType accountType) {
        super(message, cause);
        this.accountType = accountType;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}
