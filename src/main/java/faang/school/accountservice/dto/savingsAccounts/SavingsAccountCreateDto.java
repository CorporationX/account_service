package faang.school.accountservice.dto.savingsAccounts;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SavingsAccountCreateDto {
    @NotNull
    private Long accountId;

    private BigDecimal initialBalance = BigDecimal.ZERO;

    @NotNull
    private String tariffName;
}
