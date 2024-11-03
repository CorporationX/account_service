package faang.school.accountservice.util;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PercentageCalculator {

    public BigDecimal calculatePercentageNumber(@NotNull @Positive BigDecimal value,
                                                @NotNull @Positive BigDecimal percent) {
        BigDecimal multiply = value.multiply(percent);
        BigDecimal oneHundredPercent = BigDecimal.valueOf(100);
        return multiply.divide(oneHundredPercent, RoundingMode.HALF_UP);
    }
}
