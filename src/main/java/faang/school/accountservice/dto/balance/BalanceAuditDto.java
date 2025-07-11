package faang.school.accountservice.dto.balance;

import java.time.LocalDateTime;

public record BalanceAuditDto(
        String accountNumber,
        Double authBalance,
        Double actualBalance,
        Long operationId,
        LocalDateTime createdAt
) {
}
