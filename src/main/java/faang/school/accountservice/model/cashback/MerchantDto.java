package faang.school.accountservice.model.cashback;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MerchantDto(
        Long id,
        String merchantId,
        BigDecimal percentage
) {
}
