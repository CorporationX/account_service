package faang.school.accountservice.handler;

import faang.school.accountservice.dto.ErrorResponse;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountStatusException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e, HttpServletRequest request) {
        log.error("Unexpected error", e);
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception e, HttpStatus status, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

}
