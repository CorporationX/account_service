package faang.school.accountservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record BalanceAuditResponseDto(
        long id,
        long balanceId,
        long accountId,
        BigDecimal authorizedBalance,
        BigDecimal actualBalance,
        long operationId,
        LocalDateTime createdAt) {
}