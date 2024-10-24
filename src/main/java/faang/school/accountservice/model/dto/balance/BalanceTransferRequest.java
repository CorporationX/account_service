package faang.school.accountservice.model.dto.balance;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BalanceTransferRequest(
        long fromAccountId,
        long toAccountId,
        BigDecimal amount
) {
}
