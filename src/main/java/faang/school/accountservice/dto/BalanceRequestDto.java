package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceRequestDto {
    @NotNull
    private String accountNumber;
    @NotNull
    private BigDecimal authorizationBalance;
    @NotNull
    private BigDecimal factualBalance;
}
