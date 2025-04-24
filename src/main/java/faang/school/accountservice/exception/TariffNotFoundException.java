package faang.school.accountservice.exception;

public class TariffNotFoundException extends RuntimeException {

    public TariffNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
