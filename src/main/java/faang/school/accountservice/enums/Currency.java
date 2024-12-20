package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import faang.school.accountservice.exception.IllegalAccountCurrencyException;

public enum Currency {
    USD,
    EUR,
    RUB,
    CNY;

    @JsonCreator
    public static Currency toValue(String json) {
        for (Currency value : Currency.values()) {
            if (value.name().equalsIgnoreCase(json)) {
                return value;
            }
        }
        throw new IllegalAccountCurrencyException(String.format("Invalid account currency: %s", json));
    }
}
