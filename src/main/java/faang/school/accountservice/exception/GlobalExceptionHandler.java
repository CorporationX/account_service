package faang.school.accountservice.exception;

import faang.school.accountservice.dto.response.ConstraintErrorResponse;
import faang.school.accountservice.dto.response.ErrorResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage());
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

    @ExceptionHandler({InsufficientFundsException.class,
            IllegalArgumentException.class,
            EntityExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(InsufficientFundsException ex) {
        log.error(ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }
}
