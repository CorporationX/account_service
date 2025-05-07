package faang.school.accountservice.handler;

import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceAlreadyExistsException;
import faang.school.accountservice.exception.BalanceConflictException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFondException(AccountNotFoundException e) {
        log.error("Account not found: {}", e.getMessage(), e);
        ErrorResponse error = ErrorResponse.builder(e, HttpStatus.NOT_FOUND, "Account not found")
                .title("Account Not Found")
                .detail("Please verify that the account number is correct and try again.")
                .property("service", "account")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BalanceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBalanceNotFondException(BalanceNotFoundException e) {
        log.error("Balance not found: {}", e.getMessage(), e);
        ErrorResponse error = ErrorResponse.builder(e, HttpStatus.NOT_FOUND, "Balance not found")
                .title("Balance Not Found")
                .detail("Please verify that the account number is correct and try again.")
                .property("service", "account")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BalanceConflictException.class)
    public ResponseEntity<ErrorResponse> handleBalanceConflictException(BalanceConflictException e) {
        log.error("Balance conflict: {}", e.getMessage(), e);
        ErrorResponse error = ErrorResponse.builder(e, HttpStatus.CONFLICT, "Balance conflict")
                .title("Balance Conflict")
                .detail("The balance record was updated by another transaction. Please refresh and try again.\"")
                .property("service", "account")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(BalanceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleBalanceAlreadyExistsException(BalanceAlreadyExistsException e) {
        log.error("Balance already exists: {}", e.getMessage(), e);
        ErrorResponse error = ErrorResponse.builder(e, HttpStatus.CONFLICT, "Balance already exists")
                .title("Conflict: Balance Already Exists")
                .detail("A balance record for the specified account already exists. Duplicate creation is not allowed.")
                .property("service", "account")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("Unexpected runtime exception: {}", e.getMessage(), e);
        ErrorResponse error = ErrorResponse.builder(e, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error")
                .title("Internal server error")
                .detail("An unexpected error occurred. Please try again later.")
                .property("service", "account")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
