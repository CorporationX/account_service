package faang.school.accountservice.exception.non_retryable;

import faang.school.accountservice.exception.NonRetryableException;

public class EqualAccountException extends NonRetryableException {
    public EqualAccountException(String message) {
        super(message);
    }
}
