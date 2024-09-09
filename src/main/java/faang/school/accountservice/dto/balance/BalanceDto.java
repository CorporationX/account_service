package faang.school.accountservice.dto.balance;

import faang.school.accountservice.entity.account.Account;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceDto {
    @NotNull(message = "Id cannot be null")
    private Long id;

    @NotNull(message = "Account cannot be null")
    private UUID accountId;

    @NotNull(message = "Authorization balance cannot be null")
    private BigDecimal authorizationBalance;

    @NotNull(message = "Actual balance cannot be null")
    private BigDecimal actualBalance;

    @NotNull(message = "Version cannot be null")
    private long version;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
