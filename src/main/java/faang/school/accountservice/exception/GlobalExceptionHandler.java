package faang.school.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

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
}