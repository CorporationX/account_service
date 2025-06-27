package faang.school.accountservice.exception.currency;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class CurrencyNotFoundException extends EntityNotFoundException {
    public CurrencyNotFoundException(UUID currencyId) {
        super(String.format("Currency with %s not found", currencyId.toString()));
    }
}
