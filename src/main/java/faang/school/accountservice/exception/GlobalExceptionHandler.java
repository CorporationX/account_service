package faang.school.accountservice.exception;

import faang.school.accountservice.model.dto.Error;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity not found exception occurred", e);
        return new Error(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleEntityNotFoundException(IllegalArgumentException e) {
        log.error("Entity not found exception occurred", e);
        return new Error(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleInsufficientBalanceException(InsufficientBalanceException e) {
        log.error("Insufficient balance exception occurred", e);
        return new Error(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }
}
