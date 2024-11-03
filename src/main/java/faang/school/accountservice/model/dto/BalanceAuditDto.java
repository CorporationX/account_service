package faang.school.accountservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BalanceAuditDto {
    private long id;
    private long accountId;
    private long balanceAuditVersion;
    private long authorizedBalance;
    private long actualBalance;
    private long operationId;
    private LocalDateTime createdAt;
}
