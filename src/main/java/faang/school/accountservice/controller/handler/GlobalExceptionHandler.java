package faang.school.accountservice.controller.handler;

import faang.school.accountservice.dto.ErrorModel;
import faang.school.accountservice.exception.AccountAccessDeniedException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${service.name}")
    private String serviceName;

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorModel handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found exception", ex);
        return buildError(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(AccountAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorModel handleAccountAccessDeniedException(AccountAccessDeniedException ex) {
        log.error("Account access denied exception", ex);
        return buildError(ex.getMessage(), HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorModel handleFeignException(FeignException ex) {
        log.error("Feign exception", ex);
        return buildError(ex.getMessage(), HttpStatus.BAD_GATEWAY.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorModel handleGenericException(Exception ex) {
        log.error("Internal server error exception", ex);
        return buildError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ErrorModel buildError(String message, int statusCode) {
        return new ErrorModel(message, statusCode, serviceName);
    }
}
