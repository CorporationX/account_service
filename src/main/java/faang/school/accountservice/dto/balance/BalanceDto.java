package faang.school.accountservice.dto.balance;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record BalanceDto(
        UUID accountId,
        BigDecimal authorized,
        BigDecimal actual,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        Long version
) {
}
