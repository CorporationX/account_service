package faang.school.accountservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class BalanceDto {
    private Long accountId;

    @NotNull(message = "authorizedBalance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "authorizedBalance must be >= 0")
    private BigDecimal authorizedBalance;

    @NotNull(message = "actualBalance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "actualBalance must be >= 0")
    private BigDecimal actualBalance;
}
