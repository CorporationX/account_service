package faang.school.accountservice.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAuditDto {
    private Long id;
    private Long accountId;
    private Long balanceVersion;
    private Long authorizedBalance;
    private Long actualBalance;
    private Long requestId;
    private Long operationId;
    private LocalDateTime createdAt;
}