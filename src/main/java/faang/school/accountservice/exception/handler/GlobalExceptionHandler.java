package faang.school.accountservice.exception.handler;


import faang.school.accountservice.exception.DuplicateIdempotencyKeyException;
import faang.school.accountservice.exception.LockedRequestException;
import faang.school.accountservice.exception.PublishEventException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateIdempotencyKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateIdempotencyKeyException(DuplicateIdempotencyKeyException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(LockedRequestException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ErrorResponse handleLockedRequestException(LockedRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(PublishEventException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlePublishEventException(PublishEventException e) {
        return new ErrorResponse(e.getMessage());
    }
}
