package faang.school.accountservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TariffRequestDto {

    @Valid
    @NotNull
    private TariffDto tariffDto;

    @Valid
    @NotNull
    private SavingsAccountDto savingsAccountDto;
}
