package faang.school.accountservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountBalanceDto {
    private Long id;
    private Long accountId;
    private String accountNumber;
    private BigDecimal authorizedBalance;
    private BigDecimal availableBalance;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
