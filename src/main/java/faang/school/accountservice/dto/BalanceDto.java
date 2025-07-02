package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceDto(
        UUID id,
        UUID accountId,
        BigDecimal balance,
        Currency currency
) {
}