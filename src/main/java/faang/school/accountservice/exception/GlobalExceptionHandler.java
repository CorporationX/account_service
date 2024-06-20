package faang.school.accountservice.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Error> handleDataValidationException(DataValidationException e) {
        return ResponseEntity.badRequest().body(new Error(e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Error> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.badRequest().body(new Error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Error>> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<Error> exceptionList = methodArgumentNotValidException.getBindingResult()
                .getAllErrors().stream()
                .map(e -> new Error(e.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(exceptionList);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().body(new Error(e.getMessage()));
    }
}
