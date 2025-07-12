package faang.school.accountservice.dto.balance_audit_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceAuditDto {
    private Long id;
    private Long balanceVersion;
    private Long balanceChangeId;
    private LocalDateTime createdAt;
}
