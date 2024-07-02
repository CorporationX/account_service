package faang.school.accountservice.exception;

import faang.school.accountservice.dto.Error;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Error> handleDataValidationException(DataValidationException e) {
        return ResponseEntity.badRequest().body(new Error("Wrong data", e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Error> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.badRequest().body(new Error("No such element", e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Error> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.internalServerError().body(new Error("Data operation failed", e.getMessage()));
    }

    @ExceptionHandler(DataOperationException.class)
    public ResponseEntity<Error> handleDataOperationException(DataOperationException e) {
        return ResponseEntity.internalServerError().body(new Error("Data operation failed", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Error>> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<Error> exceptionList = methodArgumentNotValidException.getBindingResult().getAllErrors().stream()
                .map(error -> new Error("Invalid data passed", error.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(exceptionList);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().body(new Error("Constraint violated", e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Error> handleDataValidationException(EntityNotFoundException e) {
        return ResponseEntity.badRequest().body(new Error("Entity not found", e.getMessage()));
    }
}
