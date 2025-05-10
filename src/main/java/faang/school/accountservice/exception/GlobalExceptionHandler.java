package faang.school.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;
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
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessException.class)
    public ResponseEntity<Map<String, String>> handleAccessException(AccessException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "Ошибка", "Доступ запрещен",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(BalanceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBalanceNotFound(BalanceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "Ошибка", "Баланс не найден",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(NotEnoughFundsException.class)
    public ResponseEntity<Map<String, String>> handleNotEnoughFunds(NotEnoughFundsException e) {
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "Ошибка", "Недостаточно средств",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(SelfPayException.class)
    public ResponseEntity<Map<String, String>> handleSelfPay(SelfPayException e) {
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "Ошибка", "Недопустимый платеж",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(WrongAmountException.class)
    public ResponseEntity<Map<String, String>> handleWrongAmount(WrongAmountException e) {
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "Ошибка", "Неверная сумма",
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of(
                        "Ошибка", "Внутренняя ошибка сервера",
                        "message", e.getMessage() != null ? e.getMessage() : "Неизвестная ошибка"
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "Ошибка", "Account или UserContext не инициализированы",
                        "message", e.getMessage()
                ));
    }
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

