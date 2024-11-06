package faang.school.accountservice.dto.savings_account;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenSavingsAccountDto {
    @NotNull(message = "Account id cannot be null")
    private UUID accountId;
    @NotNull(message = "Tariff type cannot be null")
    private Long tariffId;
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;
}
