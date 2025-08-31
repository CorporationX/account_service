package faang.school.accountservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAuditDto {
    private Long id;
    private Long accountId;
    private UUID requestId;
    private BigDecimal changeAmount;
    private String currency;
    private BigDecimal oldBalance;
    private BigDecimal newBalance;
    private String eventType;
    private LocalDateTime createdAt;
}