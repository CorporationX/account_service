package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BalanceUpdateRequestDto {
    @Positive
    private long id;
    @PositiveOrZero
    @Digits(integer = 19, fraction = 2)
    private BigDecimal authorisationBalance;
    @PositiveOrZero
    @Digits(integer = 19, fraction = 2)
    private BigDecimal factualBalance;
}
