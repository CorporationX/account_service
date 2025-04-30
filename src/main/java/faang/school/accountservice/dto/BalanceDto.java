package faang.school.accountservice.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDto {

    @NotNull
    private Long accountId;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 16, fraction = 2)
    private BigDecimal currentBalance;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 16, fraction = 2)
    private BigDecimal availableBalance;

    @PastOrPresent
    private LocalDateTime updatedAt;
}
