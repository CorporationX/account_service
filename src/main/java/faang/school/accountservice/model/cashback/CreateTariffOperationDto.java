package faang.school.accountservice.model.cashback;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateTariffOperationDto(
        @Positive
        Long id,
        BigDecimal percentage
) {
}
