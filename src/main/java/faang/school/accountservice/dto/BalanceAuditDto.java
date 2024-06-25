package faang.school.accountservice.dto;

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
    private long authorizedBalance;
    private long actualBalance;
    private long balanceAuditVersion;
    private long operationId;
    private LocalDateTime auditTimeStamp;

}