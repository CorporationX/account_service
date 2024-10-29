package faang.school.accountservice.dto.Balance;

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
public class BalanceDtoWhenCreate {

    @PositiveOrZero
    private double authorizationBalance;

    @PositiveOrZero
    private double actualBalance;
}
