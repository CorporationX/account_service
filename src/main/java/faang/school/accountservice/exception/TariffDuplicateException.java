package faang.school.accountservice.exception;

public class TariffDuplicateException extends RuntimeException {

    public TariffDuplicateException(String message, Object... args) {
        super(String.format(message, args));
    }
}
