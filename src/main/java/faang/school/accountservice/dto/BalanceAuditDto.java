package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceAuditDto {

    private long id;
    private long accountId;
    private BigDecimal authorizeBalance;
    private BigDecimal actualBalance;
    private Operation operation;
    private LocalDateTime createdAt;
    private Integer versionBalance;
}
