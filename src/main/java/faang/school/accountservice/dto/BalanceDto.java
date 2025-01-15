package faang.school.accountservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BalanceDto(String accountNumber,
                         BigDecimal authorizedBalance,
                         BigDecimal actualBalance
) {}
