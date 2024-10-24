package faang.school.accountservice.model.dto.balance;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransferResultDto(
        BigDecimal fromBalance,
        BigDecimal toBalance
) {
}
