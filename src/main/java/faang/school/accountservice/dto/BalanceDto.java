package faang.school.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BalanceDto {
    private Long id;
    private String accountNumber;
    private BigDecimal authorizationBalance;
    private BigDecimal actualBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}