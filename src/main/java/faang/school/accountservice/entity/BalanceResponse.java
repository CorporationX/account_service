package faang.school.accountservice.entity;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Getter
public class BalanceResponse {
    private Long accountId;
    private BigDecimal authorizedBalance;
    private BigDecimal actualBalance;
    private BigDecimal availableBalance;
    private ZonedDateTime lastUpdated;
}
