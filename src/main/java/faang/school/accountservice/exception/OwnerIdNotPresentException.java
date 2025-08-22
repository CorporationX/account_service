package faang.school.accountservice.exception;

import faang.school.accountservice.enums.ErrorType;
import lombok.Getter;

public class OwnerIdNotPresentException extends RuntimeException {
    @Getter
    private ErrorType errorType;

    public OwnerIdNotPresentException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
