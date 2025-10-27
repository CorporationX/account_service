package faang.school.accountservice.handler;

import faang.school.accountservice.exception.AccountNumberGenerationException;
import faang.school.accountservice.exception.AccountTypeNotInitializedException;
import faang.school.accountservice.exception.DataNotFoundException;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.UnsupportedAccountTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleDataNotFound(DataNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleDataValidationException(DataValidationException e) {
        log.error("DataValidationException ", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNumberGenerationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleAccountNumberGenerationException(AccountNumberGenerationException e) {
        log.error("Failed to generate account number", e);
        return new ResponseEntity<>(
                "Unable to process your request due to high system load. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(AccountTypeNotInitializedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleAccountTypeNotInitialized(AccountTypeNotInitializedException e) {
        log.error("AccountTypeNotInitializedException occurred", e);
        return new ResponseEntity<>(
                "Service temporarily unavailable. Please contact support.",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(UnsupportedAccountTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleUnsupportedAccountType(UnsupportedAccountTypeException e) {
        log.error("Unsupported account type", e);
        return new ResponseEntity<>(
                "The requested account type is not supported",
                HttpStatus.BAD_REQUEST
        );
    }
}