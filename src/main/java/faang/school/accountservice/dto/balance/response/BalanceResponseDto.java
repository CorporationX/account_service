package faang.school.accountservice.dto.balance.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BalanceResponseDto(
        UUID id,
        String accountNumber,
        BigDecimal authBalance,
        BigDecimal currentBalance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
