package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentDto(
        @NotNull
        long paymentNumber,
        @NotNull
        Long balanceId,
        @NotNull
        PaymentStep paymentStep,
        @NotNull
        BigDecimal value
) {


}
