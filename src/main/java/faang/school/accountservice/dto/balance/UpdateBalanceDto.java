package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.Min;

public record UpdateBalanceDto(
        @Min(1)
        Long id,

        Long authorizationBalance,
        Long actualBalance
) {
}
