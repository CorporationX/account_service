package faang.school.accountservice.exception.free_account_numbers_exception;

public class CounterIncrementException extends AccountNumberGenerationException {
    public CounterIncrementException(String message) {
        super(message);
    }
}
