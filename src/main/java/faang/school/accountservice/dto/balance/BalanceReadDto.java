package faang.school.accountservice.dto.balance;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class BalanceReadDto {
    private long id;
    private long account_id;
    private BigDecimal authorizedBalance;
    private BigDecimal actualBalance;
}
