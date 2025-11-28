package faang.school.accountservice.dto.money;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.enums.Currency;

import java.math.BigDecimal;

public record Money(
        @JsonProperty(value = "amount", required = true)
        BigDecimal amount,
        @JsonProperty(value = "currency", required = true)
        Currency currency
) {
}
