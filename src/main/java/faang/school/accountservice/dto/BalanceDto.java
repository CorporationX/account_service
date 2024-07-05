package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDto {
    private Long id;
    @NotNull
    private Long accountId;
    @NotNull
    private BigDecimal authorizationBalance;
    @NotNull
    private BigDecimal actualBalance;
}
