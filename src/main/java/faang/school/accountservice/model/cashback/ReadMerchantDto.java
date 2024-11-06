package faang.school.accountservice.model.cashback;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReadMerchantDto(
        Long id,
        String merchantName,
        BigDecimal percentage
) {
}
