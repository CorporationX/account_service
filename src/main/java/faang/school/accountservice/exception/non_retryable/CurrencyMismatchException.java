package faang.school.accountservice.exception.non_retryable;

import faang.school.accountservice.exception.NonRetryableException;

public class CurrencyMismatchException extends NonRetryableException {
    public CurrencyMismatchException(String message) {
        super(message);
    }
}
