package faang.school.accountservice.exception.handler;

import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.DataConversionException;
import faang.school.accountservice.exception.ErrorMessage;
import faang.school.accountservice.exception.InvalidUserException;
import faang.school.accountservice.exception.RequestNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
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

    @ExceptionHandler(DataConversionException.class)
    public ResponseEntity<ErrorMessage> handleConversation(DataConversionException e) {
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleRequestNotFound(RequestNotFoundException e) {
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorMessage> handleInvalidUser(InvalidUserException e) {
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
