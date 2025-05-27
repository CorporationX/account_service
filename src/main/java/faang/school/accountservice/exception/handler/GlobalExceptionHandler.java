package faang.school.accountservice.exception.handler;

import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.ConcurrentModificationException;
import faang.school.accountservice.exception.DataConversionException;
import faang.school.accountservice.exception.ErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import faang.school.accountservice.exception.InvalidUserException;
import faang.school.accountservice.exception.RecipientNotFoundException;
import faang.school.accountservice.exception.RequestNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(AccountNotFoundException e) {
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(JpaOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorMessage> handleOptimisticLock(JpaOptimisticLockingFailureException e) {
        ErrorMessage errorMessage = new ErrorMessage("Account was updated concurrently. Try again.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessage> handleIllegalState(IllegalStateException e) {
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(ConcurrentModificationException.class)
    public ResponseEntity<ErrorMessage> handleConcurrentModificationException(ConcurrentModificationException e) {
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(DataConversionException.class)
    public ResponseEntity<ErrorMessage> handleConversation(DataConversionException e) {
        log.error("Data conversion error occurred: {}", e.getMessage(), e);
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleRequestNotFound(RequestNotFoundException e) {
        log.warn("Request not found: {}. StackTrace: {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorMessage> handleInvalidUser(InvalidUserException e, WebRequest request) {
        log.error("Invalid user operation. Request ID: {}, Error: {}",
                request.getHeader("X-Request-ID"), e.getMessage(), e);
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(RecipientNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleRecipientNotFound(RecipientNotFoundException e) {
        log.error("Email recipient not found in UserService. Error: {}", e.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }
}
