package faang.school.accountservice.exception;

public final class ExceptionMessage {

    private ExceptionMessage() {
    }

    public final static String INCORRECT_ENUM = "Bad enum passed: ";
    public final static String CHECK_NUMBER_EXCEPTION = "Account with this number already exists - ";
    public final static String CHANGE_STATUS_EXCEPTION = "Попытка изменить статус уже закрытого аккаунта: ";
    public final static String CHECK_ACCOUNT_BY_NUMBER_EXCEPTION = "Аккаунта по такому номеру не существует - ";
}
