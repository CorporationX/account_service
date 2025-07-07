package faang.school.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceDto {
    private Long id;
    private String accountNumber;
    private BigDecimal authorizationBalance;
    private BigDecimal actualBalance;
}