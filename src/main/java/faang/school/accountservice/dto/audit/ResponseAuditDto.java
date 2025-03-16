package faang.school.accountservice.dto.audit;

import faang.school.accountservice.enums.AuditEventType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ResponseAuditDto(
        Long userId,
        String username,
        String email,
        Long balanceAuditId,
        AuditEventType auditEventType,
        BigDecimal currentAuthAmount,
        BigDecimal previousAuthAmount,
        BigDecimal currentFactAmount,
        BigDecimal previousFactAmount,
        LocalDateTime auditedAt) {
}
