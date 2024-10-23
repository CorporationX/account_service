package faang.school.accountservice.dto.savings_account;

import faang.school.accountservice.model.enumeration.TariffType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTariffDto {
    @NotNull
    private Long savingsAccountId;

    @NotNull
    private TariffType newTariffType;
}
