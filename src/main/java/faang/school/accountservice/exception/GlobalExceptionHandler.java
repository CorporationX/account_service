package faang.school.accountservice.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleDataValidationException(EntityNotFoundException e) {
        return ResponseEntity.badRequest().body(buildExceptionMessage(e.getMessage()));
    }

    private Map<String, String> buildExceptionMessage(String message) {
        return Map.of("description", message);
    }
}
