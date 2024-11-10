package faang.school.accountservice.exception;

import faang.school.accountservice.dto.response.ConstraintErrorResponse;
import faang.school.accountservice.dto.response.ErrorResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            EntityExistsException.class,
            InsufficientFundsException.class,
            IllegalArgumentException.class,
            IllegalAccountTypeForOwner.class,
            NotUniqueAccountNumberException.class,
            IllegalAccountStatus.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleExceptionWithBadRequest(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleExceptionWithNotFound(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ConstraintErrorResponse handleConstraintValidationException(ConstraintViolationException ex) {
        final List<Violation> violations = ex.getConstraintViolations().stream()
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();
        log.error(ex.getMessage(), ex);
        return new ConstraintErrorResponse(violations);
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenAccessException(ForbiddenAccessException ex) {
        log.error(ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ConstraintErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        final List<Violation> violations = new ArrayList<>(ex.getBindingResult().getFieldErrors().stream()
                .map(violation -> new Violation(
                        violation.getField(),
                        violation.getDefaultMessage()
                ))
                .toList());
        log.error(ex.getMessage(), ex);
        return new ConstraintErrorResponse(violations);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(Throwable ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }
}
