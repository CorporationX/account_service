package faang.school.accountservice.exception.non_retryable;

import faang.school.accountservice.exception.NonRetryableException;

public class EntityNotFoundException extends NonRetryableException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
