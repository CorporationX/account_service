package faang.school.accountservice.handler;

import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountNumberGenerationException;
import faang.school.accountservice.exception.ConflictException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException e) {
        log.error("The account has been updated by another process.", e);
        return new ErrorResponse("The account has been updated by another process.", e.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAccountNotFoundException(AccountNotFoundException e) {
        log.error("Account not found", e);
        return new ErrorResponse("Account not found", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(IllegalArgumentException e) {
        log.error("Invalid account ID", e);
        return new ErrorResponse("BadRequest", e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity not found", e);
        return new ErrorResponse("Entity not found", e.getMessage());
    }

    @ExceptionHandler(AccountNumberGenerationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAccountNumberGenerationException(AccountNumberGenerationException e) {
        log.error("Error generating account number for account type: {}", e.getAccountType(), e);
        return new ErrorResponse(
            "Error generating account number",
            String.format("Account number generation failed for account type: %s. %s",
                e.getAccountType(), e.getMessage())
        );
    }
}
