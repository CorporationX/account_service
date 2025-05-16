package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record Money(
        @JsonProperty(value = "amount", required = true)
        BigDecimal amount,
        @JsonProperty(value = "currency", required = true)
        Currency currency
) {
}
