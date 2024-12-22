package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record PaymentDto(
        @NotNull
        Long balanceId,
        @NotNull
        PaymentStep paymentStep,
        @NotNull
        BigDecimal value
) {
}