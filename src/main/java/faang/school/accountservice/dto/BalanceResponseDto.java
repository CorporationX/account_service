package faang.school.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BalanceResponseDto(
        Long id,
        Long accountId,
        BigDecimal authorizedBalance,
        BigDecimal actualBalance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
