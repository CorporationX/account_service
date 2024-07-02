package faang.school.accountservice.dto.balance_audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAuditFilterDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
