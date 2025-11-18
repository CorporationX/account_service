package faang.school.accountservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = Map.of(
            DataValidationException.class, HttpStatus.BAD_REQUEST,
            EntityNotFoundException.class, HttpStatus.NOT_FOUND,
            ForbiddenException.class, HttpStatus.FORBIDDEN,
            MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST,
            IllegalArgumentException.class, HttpStatus.BAD_REQUEST,
            ConstraintViolationException.class, HttpStatus.BAD_REQUEST
    );

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException e, HttpServletRequest rq) {
        log.error("[{} {}] -> Feign client error: {}", rq.getMethod(), rq.getRequestURL(), e.getMessage());

        HttpStatus status = HttpStatus.resolve(e.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ErrorResponse errorResponse;
        String responseBody = e.contentUTF8();

        try {
            errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
        } catch (Exception ex) {
            errorResponse = new ErrorResponse(
                    LocalDateTime.now(),
                    rq.getRequestURL().toString(),
                    "FeignException",
                    e.getMessage(),
                    status.value()
            );
        }

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest rq) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(e.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        String message = buildMessage(e, status);
        log.error("[{} {}] -> {}", rq.getMethod(), rq.getRequestURL(), e.getMessage(), e);

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                rq.getRequestURL().toString(),
                e.getClass().getSimpleName(),
                message,
                status.value()
        );

        return ResponseEntity.status(status).body(error);
    }

    private String buildMessage(Exception e, HttpStatus status) {
        if (status.is4xxClientError()) {
            if (e instanceof MethodArgumentNotValidException ex) {
                return ex.getBindingResult().getAllErrors().stream()
                        .map(error -> {
                            if (error instanceof FieldError fieldError) {
                                return "%s: %s".formatted(fieldError.getField(), fieldError.getDefaultMessage());
                            }
                            return error.getDefaultMessage();
                        })
                        .collect(Collectors.joining("; "));
            }
            return e.getMessage();
        }
        return "Internal server error";
    }

}


