package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDtoWhenUpdate {

    @Positive
    private long id;

    @PositiveOrZero
    private double authorizationBalance;

    @PositiveOrZero
    private double actualBalance;
}
