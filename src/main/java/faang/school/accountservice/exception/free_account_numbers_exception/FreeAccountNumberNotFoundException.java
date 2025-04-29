package faang.school.accountservice.exception.free_account_numbers_exception;

public class FreeAccountNumberNotFoundException extends AccountNumberException {
    public FreeAccountNumberNotFoundException(String message) {
        super(message);
    }
}