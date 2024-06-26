package faang.school.accountservice.handler;

import faang.school.accountservice.exception.TypeNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(
                e.getFieldErrors().stream()
                        .map(fieldError -> Map.of(
                                fieldError.getField(),
                                String.format("%s. Actual value: %s", fieldError.getDefaultMessage(), fieldError.getRejectedValue())
                        ))
                        .toList()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest()
                .body(new HashMap<>() {{
                    put("message", "Some of fields is empty");
                    put("details", e.getMessage());
                }});
    }

    @ExceptionHandler(TypeNotFoundException.class)
    public ResponseEntity<?> handleTypeNotFoundException(TypeNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest()
                .body(new HashMap<>() {{
                    put("message", "Number of type is wrong");
                    put("details", e.getMessage());
                }});
    }
}