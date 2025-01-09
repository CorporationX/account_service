package faang.school.accountservice.dto.balanceaudit;

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
public class BalanceAuditDto {
    private Long id;
    @NotNull
    private Long accountId;
    @NotNull
    private Long versionId;
    @NotNull
    private BigDecimal authorizationBalance;
    @NotNull
    private BigDecimal currentBalance;
    @NotNull
    private String transactionId;
}
