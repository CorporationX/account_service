package faang.school.accountservice.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {ConstraintViolationException.class,
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class,            })
    public ResponseEntity<ErrorResponse> handleExceptions(Exception ex, HttpServletRequest request) {
        String errorMessage = ex.getMessage();
        if (ex.getMessage().contains("JSON parse error:")) {
            errorMessage = errorMessage.replaceAll("`[^`]*`", "");
        }
        log.error("{}: {}", ex.getClass(), errorMessage);
        ErrorResponse errorResponse = getErrorResponse(request, HttpStatus.BAD_REQUEST, errorMessage);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception ex, HttpServletRequest request) {
        String errorMessage = ex.getMessage();
        log.error("{}: {}", ex.getClass(), errorMessage);
        ErrorResponse errorResponse = getErrorResponse(request, HttpStatus.NOT_FOUND, errorMessage);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleIllegalStateException(Exception ex, HttpServletRequest request) {
        String errorMessage = ex.getMessage();
        log.error("{}: {}", ex.getClass(), errorMessage);
        ErrorResponse errorResponse = getErrorResponse(request, HttpStatus.CONFLICT, errorMessage);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    private static ErrorResponse getErrorResponse(HttpServletRequest request,HttpStatus status, String errorMessage) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setMessage(errorMessage);
        errorResponse.setPath(request.getRequestURI());
        return errorResponse;
    }
}
