package faang.school.accountservice.exception;

public class TariffNotFoundException extends RuntimeException {
    public TariffNotFoundException(Long tariffId) {
        super("Tariff with ID " + tariffId + " not found");
    }
}