package faang.school.accountservice.exception;

import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = Map.of(
            ObjectOptimisticLockingFailureException.class, HttpStatus.CONFLICT,
            MethodArgumentTypeMismatchException.class, HttpStatus.BAD_REQUEST,
            EnumConstantNotPresentException.class, HttpStatus.BAD_REQUEST,
            MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST,
            HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST,
            IllegalArgumentException.class, HttpStatus.BAD_REQUEST,
            DataIntegrityViolationException.class, HttpStatus.BAD_REQUEST,
            ConstraintViolationException.class, HttpStatus.BAD_REQUEST,
            NoSuchElementException.class, HttpStatus.NOT_FOUND,
            EntityNotFoundException.class, HttpStatus.NOT_FOUND
    );

    private static final Map<Class<? extends Exception>, String> EXCEPTION_DEFAULT_MESSAGE_MAP = Map.of(
            ObjectOptimisticLockingFailureException.class, "The account was updated by another process. Please try again.",
            MethodArgumentTypeMismatchException.class, "Invalid request parameter. Please provide the correct format.",
            EnumConstantNotPresentException.class, "Invalid request parameter. Please provide the correct format.",
            DataIntegrityViolationException.class, "User not found.  Please check it again.",
            HttpMessageNotReadableException.class, "Cannot parse JSON data. Please check it again."
    );

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        String message = getErrorMessage(ex);

        log.error("Exception caught: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        if (ex instanceof MethodArgumentNotValidException validationEx) {
            return ResponseEntity.status(status).body(buildValidationExMap(validationEx));
        }

        return ResponseEntity.status(status).body(Map.of("error", message));
    }

    private Map<String, String> buildValidationExMap(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(err -> {
                    if (err instanceof FieldError fieldError) {
                        errors.put(fieldError.getField(), err.getDefaultMessage());
                    }
                });
        return errors;
    }

    private String getErrorMessage(Exception ex) {
        if (EXCEPTION_DEFAULT_MESSAGE_MAP.containsKey(ex.getClass())) {
            return EXCEPTION_DEFAULT_MESSAGE_MAP.get(ex.getClass());
        }
        return ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.";
    }
}
