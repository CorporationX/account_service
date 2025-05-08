package faang.school.accountservice.exception;

import faang.school.accountservice.dto.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.CompletionException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AccountNotFoundException.class,
            TariffNotFoundException.class
    })
    public ResponseEntity<Error> handleExceptionWithStatusNotFound(Exception e) {
        return ResponseEntity.status(NOT_FOUND).body(getErrorResponse(e));
    }

    @ExceptionHandler({
            SavingsAccountDuplicateException.class,
            TariffDuplicateException.class
    })
    public ResponseEntity<Error> handleExceptionWithStatusConflict(Exception e) {
        return ResponseEntity.status(CONFLICT).body(getErrorResponse(e));
    }

    @ExceptionHandler({
            RetryableException.class
    })
    public ResponseEntity<Error> handleExceptionWithStatusServiceUnavailable(RetryableException ex) {
        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(getErrorResponse(ex));
    }

    @ExceptionHandler({
            CompletionException.class
    })
    public ResponseEntity<Error> handleExceptionWithStatusInternalServerError(CompletionException ex) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(getErrorResponse(ex));
    }

    private Error getErrorResponse(Exception e) {
        log.error("{}", e.toString());
        return Error.builder()
                .message(e.getMessage())
                .build();
    }
}
