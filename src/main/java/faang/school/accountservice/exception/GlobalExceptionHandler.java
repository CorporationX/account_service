package faang.school.accountservice.exception;

import faang.school.accountservice.dto.ErrorResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок валидации входных данных.
     * Возвращает JSON с полями, которые не прошли валидацию, и сообщениями из аннотаций.
     * Возвращает 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Validation failed for fields: {}",
                ex.getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getField)
                        .toList()
        );

        return ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (msg1, msg2) -> msg1 // если несколько ошибок на одно поле, берём первую
                ));
    }

    /**
     * Некорректные аргументы вызывающей стороны (ошибка клиента).
     * Возвращает 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return new ErrorResponse("bad_request", ex.getMessage());
    }

    /**
     * Обработка ошибок "сущность не найдена".
     * Возвращает 404 Not Found.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        return new ErrorResponse("entity_not_found", ex.getMessage());
    }

    /**
     * Ошибки внешних сервисов (user-service, project-service).
     * Возвращает 500 Internal Server Error.
     */
    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFeignException(FeignException ex) {
        log.error("External service error: {}", ex.getMessage());
        return new ErrorResponse("external_service_error", "External service error occurred");
    }

    /**
     * Исключение, возникающее при попытке выполнить некорректный переход статуса запроса.
     * Возвращает 400 BAD_REQUEST.
     */
    @ExceptionHandler(IllegalStatusTransitionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalStatusTransitionException(IllegalStatusTransitionException ex) {
        log.warn("Illegal status transition: {}", ex.getMessage());
        return new ErrorResponse("illegal_status_transition", ex.getMessage());
    }

    /**
     * Обработка ошибок нарушения уникальности (дубликаты lockValue или idempotencyToken).
     * Возвращает 409 CONFLICT.
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateKeyException(DuplicateKeyException  ex) {
        log.warn("Duplicate key violation: {}", ex.getMessage());
        return new ErrorResponse("duplicate_key", ex.getMessage());
    }

    /**
     * Обработка ошибок публикации событий в Kafka.
     * Возвращает 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(EventPublishException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleEventPublishException(EventPublishException ex) {
        log.error("Event publishing failed: {}", ex.getMessage());
        return new ErrorResponse("event_publish_error", "Failed to publish notification event");
    }
}