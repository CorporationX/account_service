package faang.school.accountservice.exception.handler;

import faang.school.accountservice.dto.exception.ErrorResponse;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.exception.InsufficientBalanceException;
import faang.school.accountservice.exception.InvalidBalanceOperationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                               HttpServletRequest request) {

        FieldError fieldError = ex.getBindingResult().getFieldError();

        String message = fieldError != null
                ? fieldError.getDefaultMessage()
                : "Validation failed";

        log.error("Validation error: {}", message);

        return build(HttpStatus.BAD_REQUEST, "Validation Error", message, request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex,
                                                                            HttpServletRequest request) {

        String message = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("Invalid request");

        log.error("Constraint violation: {}", message);

        return build(HttpStatus.BAD_REQUEST, "Constraint Violation", message, request.getRequestURI());
    }

    @ExceptionHandler(BalanceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBalanceNotFoundException(BalanceNotFoundException ex,
                                                                        HttpServletRequest request) {

        log.error("Balance not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "Balance Not Found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex,
                                                                        HttpServletRequest request) {

        log.error("Account not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "Account Not Found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException ex,
                                                                            HttpServletRequest request) {

        log.error("Insufficient balance: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Insufficient Balance", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidBalanceOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBalanceOperationException(InvalidBalanceOperationException ex,
                                                                                HttpServletRequest request) {

        log.error("Invalid balance operation: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Invalid Operation", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {

        log.error("Unexpected error", ex);

        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                ex.getMessage(), request.getRequestURI());
    }


    private ResponseEntity<ErrorResponse> build(HttpStatus status,
                                                String error,
                                                String message,
                                                String path) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}