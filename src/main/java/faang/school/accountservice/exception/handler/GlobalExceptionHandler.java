package faang.school.accountservice.exception.handler;


import faang.school.accountservice.exception.DuplicateIdempotencyKeyException;
import faang.school.accountservice.exception.LockedRequestException;
import faang.school.accountservice.exception.PublishEventException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse handleIllegalStateException(IllegalStateException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

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
