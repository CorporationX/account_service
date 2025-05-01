package faang.school.accountservice.controller;

import faang.school.accountservice.exception.AccountAlreadyClosedException;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountOperationConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Глобальный обработчик исключений для REST API.
 * Обрабатывает различные типы исключений, возникающих в приложении, и возвращает соответствующие HTTP-ответы.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение, когда счет не найден.
     *
     * @param exception исключение {@link AccountNotFoundException}
     * @return ответ с кодом 404 и сообщением об ошибке
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFound(AccountNotFoundException exception) {
        log.error("Account not found: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("Account not found: %s", exception.getMessage()));
    }

    /**
     * Обрабатывает исключение конфликта операций над счетом.
     *
     * @param exception исключение {@link AccountOperationConflictException}
     * @return ответ с кодом 409 и сообщением об ошибке
     */
    @ExceptionHandler(AccountAlreadyClosedException.class)
    public ResponseEntity<String> handleAlreadyClosed(AccountAlreadyClosedException exception) {
        log.error("Account already closed: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    /**
     * Обрабатывает непредвиденные исключения.
     *
     * @param exception общее исключение {@link Exception}
     * @return ответ с кодом 500 и сообщением об ошибке
     */
    @ExceptionHandler(AccountOperationConflictException.class)
    public ResponseEntity<String> handleConflict(AccountOperationConflictException exception) {
        log.error("Account optimistic lock conflict: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    /**
     * Обрабатывает непредвиденные исключения.
     *
     * @param exception общее исключение {@link Exception}
     * @return ответ с кодом 500 и сообщением об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.error("Internal server error: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(String.format("An internal error has occurred: %s", exception.getMessage()));
    }

    /**
     * Обрабатывает исключение неверного типа аргумента метода.
     *
     * @param exception исключение {@link MethodArgumentTypeMismatchException}
     * @return ответ с кодом 400 и сообщением об ошибке
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        log.error("Invalid argument type: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(String.format("Invalid argument type: %s", exception.getMessage()));
    }
}