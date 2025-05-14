package faang.school.accountservice.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@Component("apiHandlerV2")
public class GlobalExceptionHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = Map.ofEntries(
            Map.entry(ObjectOptimisticLockingFailureException.class, HttpStatus.CONFLICT),
            Map.entry(MethodArgumentTypeMismatchException.class, HttpStatus.BAD_REQUEST),
            Map.entry(EnumConstantNotPresentException.class, HttpStatus.BAD_REQUEST),
            Map.entry(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST),
            Map.entry(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST),
            Map.entry(IllegalArgumentException.class, HttpStatus.BAD_REQUEST),
            Map.entry(IllegalStateException.class, HttpStatus.BAD_REQUEST),
            Map.entry(ConstraintViolationException.class, HttpStatus.BAD_REQUEST),
            Map.entry(DataIntegrityViolationException.class, HttpStatus.BAD_REQUEST),
            Map.entry(NoSuchElementException.class, HttpStatus.NOT_FOUND),
            Map.entry(EntityNotFoundException.class, HttpStatus.NOT_FOUND)
    );

    private static final Map<Class<? extends Exception>, String> EXCEPTION_DEFAULT_MESSAGE_MAP = Map.of(
            ObjectOptimisticLockingFailureException.class, "The resource was updated by another process. Please retry.",
            MethodArgumentTypeMismatchException.class, "Invalid request parameter format.",
            EnumConstantNotPresentException.class, "Invalid enum value provided.",
            DataIntegrityViolationException.class, "Data integrity violation.",
            HttpMessageNotReadableException.class, "Malformed JSON request."
    );

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> b));

        log.warn("⚠️ Validation failed: {}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "message", "Validation failed",
                        "errors", errors,
                        "url", request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, HttpServletRequest request) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        String message = EXCEPTION_DEFAULT_MESSAGE_MAP.getOrDefault(ex.getClass(), ex.getMessage());

        log.error("🔥 Exception caught: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        return ResponseEntity.status(status).body(
                Map.of(
                        "status", status.value(),
                        "message", message != null ? message : "Unexpected server error.",
                        "url", request.getRequestURI()
                )
        );
    }
}
