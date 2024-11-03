package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashbackBalanceDto {
    private Long balanceId;
    private BigDecimal averageBalance;

    public CashbackBalanceDto(Long balanceId, Double averageBalance) {
        this.balanceId = balanceId;
        this.averageBalance = averageBalance != null ? BigDecimal.valueOf(averageBalance) : BigDecimal.ZERO;
    }
}
