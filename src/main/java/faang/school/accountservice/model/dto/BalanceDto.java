package faang.school.accountservice.model.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BalanceDto(
        long id,

        @Positive(message = "Id must be greater than 0")
        long accountId,
        BigDecimal currentAuthorizationBalance,
        BigDecimal currentActualBalance
) {
}
