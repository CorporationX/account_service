package faang.school.accountservice.exception.non_retryable;

import faang.school.accountservice.exception.NonRetryableException;

public class NotActiveAccountException extends NonRetryableException {
    public NotActiveAccountException(String message) {
        super(message);
    }
}
