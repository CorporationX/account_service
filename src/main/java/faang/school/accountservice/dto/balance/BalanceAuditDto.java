package faang.school.accountservice.dto.balance;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceAuditDto {

    private UUID id;

    @NotNull(message = "AccountNumber it should not be null")
    private String accountNumber;

    private Long version;
    private BigDecimal authorizationBalance;
    private BigDecimal actualBalance;
    private Long operationId;
    private LocalDateTime createdAt;
}
