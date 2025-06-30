package faang.school.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountBalanceDto {
    private Long id;
    private BigDecimal authorizedBalance;
    private BigDecimal actualBalance;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
