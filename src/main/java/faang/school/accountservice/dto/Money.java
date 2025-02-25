package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.enums.CurrencyType;

import java.math.BigDecimal;

public record Money(
        @JsonProperty(value = "amount", required = true)
        BigDecimal amount,
        @JsonProperty(value = "currency", required = true)
        CurrencyType currencyType
) {
}
