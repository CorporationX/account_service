package faang.school.accountservice.exception.pending;

import faang.school.accountservice.enums.pending.OperationStatus;
import faang.school.accountservice.exception.ApiException;
import org.springframework.http.HttpStatus;

public class UnknownOperationException extends ApiException {
    private static final String MESSAGE_TEMPLATE = "Unknown operation type: %s";

    public UnknownOperationException(OperationStatus operationType) {
        super(MESSAGE_TEMPLATE, HttpStatus.BAD_REQUEST, operationType);
    }
}
