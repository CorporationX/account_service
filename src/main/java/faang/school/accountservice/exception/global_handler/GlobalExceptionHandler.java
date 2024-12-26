package faang.school.accountservice.exception.global_handler;

import faang.school.accountservice.exception.BalanceBelowZeroException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InternalError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(Exception e) {
        return buildResponse(e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(Exception e) {
        return buildResponse(e);
    }

    @ExceptionHandler({BalanceBelowZeroException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBalanceBelowZeroAndIllegalArgument(Exception e) {
        return buildResponse(e);
    }

    private ErrorResponse buildResponse(Exception e) {
        log.error(e.getMessage(), e);
        return ErrorResponse.builder()
                .message(e.getMessage())
                .error(e.getClass().getSimpleName())
                .timestamp(LocalDateTime.now())
                .build();
    }
}