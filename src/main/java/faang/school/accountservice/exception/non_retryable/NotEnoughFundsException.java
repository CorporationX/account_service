package faang.school.accountservice.exception.non_retryable;

import faang.school.accountservice.exception.NonRetryableException;

public class NotEnoughFundsException extends NonRetryableException {
    public NotEnoughFundsException(String message) {
        super(message);
    }
}
