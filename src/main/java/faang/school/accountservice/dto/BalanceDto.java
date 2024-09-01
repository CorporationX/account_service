package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.validator.CreateValidation;
import faang.school.accountservice.validator.UpdateValidation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {
    @NotNull(message = "Id can't be nullable", groups = {UpdateValidation.class})
    private UUID id;

    @NotNull(message = "Authorize balance can't be nullable", groups = {CreateValidation.class, UpdateValidation.class})
    private BigDecimal authorizedBalance;

    @NotNull(message = "Actual balance can't be nullable", groups = {CreateValidation.class, UpdateValidation.class})
    private BigDecimal actualBalance;

    @NotNull(message = "Currency can't be nullable", groups = CreateValidation.class)
    private Currency currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull(message = "Account id can't be nullable", groups = CreateValidation.class)
    private Long accountId;
}
