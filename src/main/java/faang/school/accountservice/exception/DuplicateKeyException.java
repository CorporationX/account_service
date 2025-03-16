package faang.school.accountservice.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateKeyException extends RuntimeException {
    public DuplicateKeyException(String message, DataIntegrityViolationException ex) {
        super(message, ex);
    }
}
