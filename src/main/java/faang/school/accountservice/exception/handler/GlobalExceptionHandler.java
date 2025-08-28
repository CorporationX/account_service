package faang.school.accountservice.exception.handler;

import faang.school.accountservice.exception.EntityAlreadyBlockedException;
import faang.school.accountservice.exception.EntityAlreadyClosedException;
import faang.school.accountservice.exception.EntityCancelledException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.MoreOneOwnerException;
import faang.school.accountservice.exception.NotResourceOwnerException;
import faang.school.accountservice.exception.OwnerIdNotPresentException;
import lombok.extern.slf4j.Slf4j;
import faang.school.accountservice.dto.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MoreOneOwnerException.class)
    public ResponseEntity<Error> handleMoreOneOwner(MoreOneOwnerException e) {
        log.error(e.getMessage(), e);
        HttpStatus httpStatus = e.getErrorType().getHttpStatus();
        return ResponseEntity
                .status(httpStatus)
                .body(new Error(httpStatus.name(), e.getMessage()));
    }

    @ExceptionHandler(OwnerIdNotPresentException.class)
    public ResponseEntity<Error> handleOwnerIdNotPresent(OwnerIdNotPresentException e) {
        log.error(e.getMessage(), e);
        HttpStatus httpStatus = e.getErrorType().getHttpStatus();
        return ResponseEntity
                .status(httpStatus)
                .body(new Error(httpStatus.name(), e.getMessage()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityAlreadyClosedException.class)
    public Error handleEntityAlreadyClosed(EntityAlreadyClosedException e) {
        log.error(e.getMessage(), e);
        return new Error(HttpStatus.CONFLICT.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityAlreadyBlockedException.class)
    public Error handleEntityAlreadyBlocked(EntityAlreadyBlockedException e) {
        log.error(e.getMessage(), e);
        return new Error(HttpStatus.CONFLICT.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityCancelledException.class)
    public Error handleEntityCancelled(EntityCancelledException e) {
        log.error(e.getMessage(), e);
        return new Error(HttpStatus.CONFLICT.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public Error handleEntityNotFound(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return new Error(HttpStatus.NOT_FOUND.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotResourceOwnerException.class)
    public Error handleNotResourceOwner(NotResourceOwnerException e) {
        log.error(e.getMessage(), e);
        return new Error(HttpStatus.FORBIDDEN.name(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public Error handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new Error(HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage());
    }
}
