package faang.school.accountservice.exception;

import faang.school.accountservice.enums.AccountType;

public class AccountTypeNotInitializedException extends RuntimeException {
    public AccountTypeNotInitializedException(AccountType type){
        super("Account type not initialized: " + type);
    }
}
