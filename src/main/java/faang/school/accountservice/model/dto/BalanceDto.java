package faang.school.accountservice.model.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BalanceDto(
        long id,
        long accountId,
        BigDecimal currentAuthorizationBalance,
        BigDecimal currentActualBalance
) {
}
