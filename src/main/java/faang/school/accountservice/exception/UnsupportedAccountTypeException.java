package faang.school.accountservice.exception;

import faang.school.accountservice.enums.AccountType;

public class UnsupportedAccountTypeException extends RuntimeException {
    public UnsupportedAccountTypeException(AccountType type) {
        super("Account type " + type + " does not support account number generation");
    }
}
