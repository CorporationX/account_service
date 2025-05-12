package faang.school.accountservice.controller;

import faang.school.accountservice.dto.ApiError;
import faang.school.accountservice.exception.InvalidAccountStateException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest req) {
        log.error("Entity not found: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest req) {
        log.error("Violation of restrictions: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String message = "Invalid parameter: " + ex.getName() + " must be of type " + ex.getRequiredType().getSimpleName();
        log.error("Invalid parameter type: {}", message, ex);
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                sb.append(fieldError.getField()).append(" — ").append(fieldError.getDefaultMessage())
        );
        log.error("Validation error: {}", sb, ex);
        return buildErrorResponse(sb.toString(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(InvalidAccountStateException.class)
    public ResponseEntity<ApiError> handleInvalidAccountState(InvalidAccountStateException ex, HttpServletRequest req) {
        log.error("Incorrect account status\n: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOtherExceptions(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    private ResponseEntity<ApiError> buildErrorResponse(Exception ex, HttpStatus status, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), status, request);
    }

    private ResponseEntity<ApiError> buildErrorResponse(String message, HttpStatus status, HttpServletRequest request) {
        ApiError error = new ApiError(
                message,
                status.value(),
                request.getRequestURI(),
                Instant.now().toString()
        );
        return ResponseEntity.status(status).body(error);
    }
}