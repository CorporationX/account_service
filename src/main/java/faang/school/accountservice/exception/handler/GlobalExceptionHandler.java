package faang.school.accountservice.exception.handler;

import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.EventProcessingException;
import faang.school.accountservice.exception.EventPublishingException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint Violation Exception", ex);
        return new ErrorResponse("Constraint Violation Exception", ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException ex) {
        log.error("Entity Not Found", ex);
        return new ErrorResponse("Entity Not Found", ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(NoSuchElementException ex) {
        log.error("No Such Element Exception", ex);
        return new ErrorResponse("No Such Element Exception", ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception", ex);
        return new ErrorResponse("Runtime exception", ex.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException ex) {
        log.error("Data Validation Error", ex);
        return new ErrorResponse("Data Validation Exception", ex.getMessage());
    }

    @ExceptionHandler(EventProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleEventProcessingException(EventProcessingException ex) {
        log.error("Event Processing Exception", ex);
        return new ErrorResponse("Event Processing Exception", ex.getMessage());
    }

    @ExceptionHandler(EventPublishingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleEventPublishingException(EventPublishingException ex) {
        log.error("Event Publishing Exception", ex);
        return new ErrorResponse("Event Publishing Exception", ex.getMessage());
    }
}
