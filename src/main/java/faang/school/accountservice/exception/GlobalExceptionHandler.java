package faang.school.accountservice.exception;

import faang.school.accountservice.dto.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Error> handleAccountNotFound(AccountNotFoundException e) {
        log.error("Account not found: {}", e.getMessage());
        Error error = new Error("ACCOUNT_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidAccountOperationException.class)
    public ResponseEntity<Error> handleInvalidOperation(InvalidAccountOperationException e) {
        log.error("Invalid account operation: {}", e.getMessage());
        Error error = new Error("INVALID_OPERATION", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationError(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        Error error = new Error("VALIDATION_ERROR", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGenericError(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        Error error = new Error("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}