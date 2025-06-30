package faang.school.accountservice.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AccountBalanceDto {
    private Long accountId;
    private String accountNumber;
    private BigDecimal authorisedBalance;
    private BigDecimal availableBalance;
}
