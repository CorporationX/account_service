package faang.school.accountservice.exception;

import org.springframework.http.HttpStatus;

public class DataConflictRetryException extends ApiException {
    public DataConflictRetryException(String message) {
        super(message, message);
    }

    public DataConflictRetryException(String message, String debugMessage) {
        super(message, debugMessage);
    }


    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.CONFLICT;
    }
}
