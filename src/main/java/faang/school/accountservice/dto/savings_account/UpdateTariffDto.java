package faang.school.accountservice.dto.savings_account;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTariffDto {
    @NotNull(message = "Savings account id cannot be null")
    private UUID savingsAccountId;
    @NotNull(message = "Tariff id cannot be null")
    private Long tariffId;
}
