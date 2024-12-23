package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record BalanceDto(
        @NotNull
        long id,
        @NotNull
        long accountId,
        BigDecimal authorizedValue,
        BigDecimal actualValue,
        String createdAt,
        String updatedAt,
        String version
) {
}