package faang.school.accountservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BalanceDto {
    private Long id;
    private Long accountId;
    private BigDecimal authBalance;
    private BigDecimal actualBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}