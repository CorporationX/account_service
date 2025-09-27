package faang.school.accountservice.exception;

/**
 * Исключение для случаев неверного формата номера счета
 */
public class InvalidAccountNumberException extends IllegalArgumentException {
    public InvalidAccountNumberException(String accountNumber) {
        super("Invalid account number: " + accountNumber);
    }

    public InvalidAccountNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}