package faang.school.accountservice.dto.balance;

import faang.school.accountservice.dto.PaymentOperationType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record PaymentDto(
        @NotNull
        Long balanceId,
        @NotNull
        PaymentOperationType paymentOperationType,
        @NotNull
        BigDecimal value
) {
}