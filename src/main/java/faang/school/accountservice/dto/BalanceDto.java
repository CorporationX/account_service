package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceDto {
    private Long id;
    private AccountDto account;

    @NotNull(message = "current balance can't be null")
    private BigDecimal currentBalance;

    @NotNull(message = "authorized balance can't be null")
    private BigDecimal authorizedBalance;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
