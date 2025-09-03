package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record SavingsAccountDto(
        AccountDto account,
        @NotNull
        BigDecimal balance,
        LocalDateTime lastInterestAt,
        @NotBlank
        String currentTariff,
        @NotNull
        BigDecimal currentRate,
        @NotNull
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
