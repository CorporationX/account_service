package faang.school.accountservice.dto.balance;

import java.time.LocalDateTime;

public record BalanceResponseDto(
        Long accountId,
        LocalDateTime updatedAt,
        Long authorizationAmount
) {
}