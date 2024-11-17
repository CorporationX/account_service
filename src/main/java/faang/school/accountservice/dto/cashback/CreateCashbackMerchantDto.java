package faang.school.accountservice.dto.cashback;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCashbackMerchantDto {
    @NotNull(message = "Merchant Id must not be null")
    private UUID merchantId;
    @NotNull(message = "Cashback Percentage must not be null")
    @Digits(integer = 8, fraction = 2, message = "Cashback Percentage must be a decimal with up to 10 digits and 2 decimal places")
    @DecimalMin(value = "0.01", message = "Cashback Percentage must be at least 0.01")
    @DecimalMax(value = "99.99", message = "Cashback Percentage must be less than or equal to 99.99")
    private BigDecimal cashbackPercentage;
}
