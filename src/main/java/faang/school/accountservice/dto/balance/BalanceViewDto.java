package faang.school.accountservice.dto.balance;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceViewDto {
    private Long id;
    private Long accountId;
    private BigDecimal actualBalance;
    private BigDecimal authorizedBalance;
}