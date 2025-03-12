package faang.school.accountservice.exception.handler;

import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.exception.ValidationException;
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
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {

        return handleException(e, NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validationException(ValidationException e) {

        return handleException(e, NOT_FOUND);
    }

    private ResponseEntity<String> handleException(Exception e, HttpStatus status) {
        log.error(e.getMessage(), e);

        return new ResponseEntity<>(e.getMessage(), status);
    }
}
