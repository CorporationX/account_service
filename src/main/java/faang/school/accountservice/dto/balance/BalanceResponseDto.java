package faang.school.accountservice.dto.balance;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BalanceResponseDto(
        LocalDateTime updatedAt,
        Long authorizationAmount,
        LocalDateTime createdAt
) {
}