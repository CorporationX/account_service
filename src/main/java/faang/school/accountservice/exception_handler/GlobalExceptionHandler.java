package faang.school.accountservice.exception_handler;

import faang.school.accountservice.dto.ErrorResponse;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountStatusException;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.exception.ServiceUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@Slf4j
@ControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @SuppressWarnings("unused")
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.error("Validation errors: {}", errors);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .code(VALIDATION_ERROR)
                        .message("Validation failed")
                        .details(errors)
                        .build());
    }

    @ExceptionHandler(DataValidationException.class)
    @SuppressWarnings("unused")
    public ResponseEntity<Object> handleDataValidationExceptions(DataValidationException ex) {
        log.error("Data validation error: {}", ex.getMessage(), ex);

        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .code(VALIDATION_ERROR)
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @SuppressWarnings("unused")
    public ResponseEntity<Object> handleResourceNotFoundExceptions(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .code("RESOURCE_NOT_FOUND")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @SuppressWarnings("unused")
    public ResponseEntity<Object> handleServiceUnavailableExceptions(ServiceUnavailableException ex) {
        log.error("Service unavailable: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.builder()
                        .code("SERVICE_UNAVAILABLE")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    @SuppressWarnings("unused")
    public ResponseEntity<Object> handleExceptions(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .code("INTERNAL_ERROR")
                        .message("An unexpected error occurred: %s".formatted(ex.getMessage())));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException e, HttpServletRequest request) {
        log.warn("Account not found: {} | Request URI: {}", e.getMessage(), request.getRequestURI());
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ErrorResponse> handleAccountStatus(AccountStatusException e, HttpServletRequest request) {
        log.warn("Invalid account status: {} | Request URI: {}", e.getMessage(), request.getRequestURI());
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e, HttpServletRequest request) {
        log.error("Illegal state: {}", e.getMessage());
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception e, HttpStatus status,
            HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .code(status.getReasonPhrase())
                .error(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }
}