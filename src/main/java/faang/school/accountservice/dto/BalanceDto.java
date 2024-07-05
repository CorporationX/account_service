package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {

    private Long id;
    @NotNull(message = "AccountId it should not be null")
    private Long accountId;
    private BigDecimal currentAuthorizationBalance;
    private BigDecimal currentActualBalance;
}
