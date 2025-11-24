package faang.school.accountservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiError> handleAccountNotFound(AccountNotFoundException e) {
        log.error("Account not found: {}", e.getMessage());
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "ACCOUNT_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(InvalidAccountOperationException.class)
    public ResponseEntity<ApiError> handleInvalidOperation(InvalidAccountOperationException e) {
        log.error("Invalid account operation: {}", e.getMessage());
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "INVALID_OPERATION", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationError(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage()));
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericError(Exception e) {
        log.error("Unexpected error: ", e);
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
}
