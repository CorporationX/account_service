package faang.school.accountservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BalanceDto(Long accountId,
                         BigDecimal authorizedBalance,
                         BigDecimal actualBalance
) {}
