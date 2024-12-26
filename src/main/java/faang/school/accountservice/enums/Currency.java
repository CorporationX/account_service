package faang.school.accountservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import faang.school.accountservice.exception.IllegalAccountCurrencyException;

import java.util.Arrays;

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

        throw new IllegalAccountCurrencyException(String.format(
                "Invalid account currency: %s. Valid values are: %s", json, Arrays.toString(Currency.values())));
    }
}
