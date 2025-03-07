package faang.school.accountservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record BalanceResponseDto(
        Long id,
        Long accountId,
        BigDecimal authorizedBalance,
        BigDecimal actualBalance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
