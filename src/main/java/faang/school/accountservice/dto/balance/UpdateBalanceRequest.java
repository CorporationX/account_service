package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBalanceRequest {

    @Positive
    private long id;

    @PositiveOrZero
    private BigDecimal authorizationBalance;

    @PositiveOrZero
    private BigDecimal actualBalance;
}
