package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BalanceDto {
    @NotNull
    private Long id;
    private Long accountId;
    @NotNull
    private BigDecimal authBalance;
    @NotNull
    private BigDecimal actualBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}