package faang.school.accountservice.messages;

public class ErrorMessages {
    //Account
    public static final String ACCOUNT_NOT_FOUND = "Account with number %s not found";

    //Balance
    public static final String BALANCE_NOT_FOUND = "Balance with accountNumber %s not found";
    public static final String BALANCE_CONFLICT_ERROR = "The balance was updated by another user. " +
            "Please reload and try again.";
    public static final String BALANCE_EXISTS_ERROR = "Balance already exists for account %s";
    public static final String OPTIMISTIC_LOCK_ERROR = "Optimistic lock exception occurred while updating balance";

}
