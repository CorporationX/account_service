package faang.school.accountservice.exception;

import faang.school.accountservice.enums.ErrorType;
import lombok.Getter;

public class MoreOneOwnerException extends RuntimeException {
    @Getter
    private ErrorType errorType;

    public MoreOneOwnerException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
