package faang.school.accountservice.dto.balance;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BalanceDto(
        Long accountId,
        Double actualBalance,
        LocalDateTime createdAt
) {
}
