package faang.school.accountservice.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * Обработчик исключений REST-контроллера для различных типов ошибок, возникающих при выполнении запросов.
 *
 * <p>Каждый метод обрабатывает конкретный тип исключения и возвращает клиенту
 * стандартизированный объект {@link ErrorResponse} с HTTP-статусом, сообщением и отметкой времени.</p>
 *
 * <p>Обрабатываемые исключения:
 * <ul>
 *     <li>{@link IllegalArgumentException} — некорректные аргументы запроса, возвращает {@code 400 Bad Request}</li>
 *     <li>{@link PaymentFailedException} — неуспешная попытка оплаты, возвращает {@code 402 Payment Required}</li>
 *     <li>{@link HttpMessageNotReadableException} — некорректный JSON в запросе, возвращает {@code 400 Bad Request}</li>
 *     <li>{@link MethodArgumentNotValidException} — ошибки валидации данных (@Valid), возвращает {@code 400 Bad Request}</li>
 *     <li>{@link EntityNotFoundException} — ресурс не найден, возвращает {@code 404 Not Found}</li>
 *     <li>{@link ForbiddenException} — доступ запрещён, возвращает {@code 403 Forbidden}</li>
 *     <li>{@link DataValidationException} — ошибка бизнес-валидации данных, возвращает {@code 400 Bad Request}</li>
 *     <li>{@link HttpRequestMethodNotSupportedException} — метод запроса не поддерживается, возвращает {@code 405 Method Not Allowed}</li>
 *     <li>{@link UnauthorizedException} — неавторизованный доступ, возвращает {@code 401 Unauthorized}</li>
 *     <li>{@link Exception} — любые другие необработанные исключения, возвращает {@code 500 Internal Server Error}</li>
 * </ul>
 * </p>
 *
 * <p>Каждый обработчик создаёт {@link ErrorResponse} с полями:
 * <ul>
 *     <li>status — HTTP-статус ошибки</li>
 *     <li>message — описание ошибки</li>
 *     <li>timestamp — отметка времени возникновения ошибки (UTC)</li>
 * </ul>
 * </p>
 *
 * @author agent
 * @since 31.08.2025
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentFailed(PaymentFailedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.PAYMENT_REQUIRED.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                      WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid JSON format",
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleDataValidation(DataValidationException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}