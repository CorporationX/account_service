package faang.school.accountservice.exception.handler;

import faang.school.accountservice.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return handleException(ex, NOT_FOUND);
    }

    private ResponseEntity<String> handleException(Exception exception, HttpStatus status) {
        log.error(exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), status);
    }
}
