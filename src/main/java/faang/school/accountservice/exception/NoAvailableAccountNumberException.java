package faang.school.accountservice.exception;

/**
 * Исключение для случаев отсутствия доступных номеров счетов
 */
public class NoAvailableAccountNumberException extends RuntimeException {

    public NoAvailableAccountNumberException(String message) {
        super(message);
    }

    public NoAvailableAccountNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}
