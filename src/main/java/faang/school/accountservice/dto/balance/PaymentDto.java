package faang.school.accountservice.dto.balance;

import faang.school.accountservice.enums.PaymentOperationType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentDto(
        @NotNull
        Long balanceOwnerId,
        @NotNull
        Long balanceRecipientId,
        @NotNull
        PaymentOperationType paymentOperationType,
        @NotNull
        BigDecimal value
) {
}