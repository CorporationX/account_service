package faang.school.accountservice.dto;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TariffRequestDto {

    @Valid
    private TariffDto tariffDto;

    @Valid
    private SavingsAccountDto savingsAccountDto;
}
