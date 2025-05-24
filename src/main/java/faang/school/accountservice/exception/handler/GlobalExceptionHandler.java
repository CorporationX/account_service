package faang.school.accountservice.exception.handler;

import faang.school.accountservice.exception.DuplicateIdempotencyKeyException;
import faang.school.accountservice.exception.LockedRequestException;
import faang.school.accountservice.exception.PublishEventException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
@Component("apiHandlerV1")
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(DuplicateIdempotencyKeyException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateIdempotencyKeyException(DuplicateIdempotencyKeyException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(LockedRequestException.class)
    public ResponseEntity<Map<String, Object>> handleLockedRequestException(LockedRequestException ex) {
        return createErrorResponse(HttpStatus.TOO_MANY_REQUESTS, ex);
    }

    @ExceptionHandler(PublishEventException.class)
    public ResponseEntity<Map<String, Object>> handlePublishEventException(PublishEventException ex) {
        return createErrorResponse(HttpStatus.TOO_MANY_REQUESTS, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex);
    }
    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, Exception exception) {
        String message = exception.getMessage();
        log.error("Error: {}", message, exception);
        return ResponseEntity.status(status).body(Map.of(
                "error", status.getReasonPhrase(),
                "message", message != null ? message : "Unknown error"
        ));
    }
}
