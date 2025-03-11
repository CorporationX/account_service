package faang.school.accountservice.exception;

public class NoFreeAccountNumbersException extends IllegalStateException {
    public NoFreeAccountNumbersException(String message) {
        super(message);
    }
}
