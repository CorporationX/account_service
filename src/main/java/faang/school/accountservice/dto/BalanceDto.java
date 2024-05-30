package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceDto {
    private BigDecimal actualBalance;
    private BigDecimal authorizationBalance;
    @NotNull
    private String accountNumber;
}
