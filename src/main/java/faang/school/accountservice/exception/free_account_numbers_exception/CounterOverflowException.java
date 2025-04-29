package faang.school.accountservice.exception.free_account_numbers_exception;

public class CounterOverflowException extends AccountNumberGenerationException {
    public CounterOverflowException(String message) {
        super(message);
    }
}