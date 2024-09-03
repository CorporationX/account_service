package faang.school.accountservice.exception;

public final class ExceptionMessages {

    private ExceptionMessages() {
    }

    public static final String INCORRECT_ENUM = "Bad enum passed: ";
    public static final String CHECK_NUMBER_EXCEPTION = "Account with this number already exists - ";
    public static final String CHANGE_STATUS_EXCEPTION = "Попытка изменить статус уже закрытого аккаунта: ";
    public static final String CHECK_ACCOUNT_BY_NUMBER_EXCEPTION = "Аккаунта по такому номеру не существует - ";

    // account
    public static final String ACCOUNT_NOT_FOUND = "Account with id %d not found";
}
