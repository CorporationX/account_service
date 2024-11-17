package faang.school.accountservice.dto.cashback;

import faang.school.accountservice.enums.payment.Category;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCashbackOperationTypeDto {
    @NotNull(message = "Operation Type must not be null")
    private Category operationType;
    @NotNull(message = "Cashback Percentage must not be null")
    @Digits(integer = 8, fraction = 2, message = "Cashback Percentage must be a decimal with up to 10 digits and 2 decimal places")
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "99.99")
    private BigDecimal cashbackPercentage;
}
