package faang.school.accountservice.exception;

import org.springframework.dao.DataAccessException;

public class TestDataAccessException extends DataAccessException {
    public TestDataAccessException() {
        super("Test exception");
    }
}
