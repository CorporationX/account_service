package faang.school.accountservice.model.cashback;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateTariffMerchantDto(
        @Positive
        Long id,
        BigDecimal percentage
) {
}
