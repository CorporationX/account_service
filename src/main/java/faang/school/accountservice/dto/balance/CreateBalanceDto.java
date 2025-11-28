package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.Min;

public record CreateBalanceDto(
        @Min(1)
        Long accountId
) {
}
