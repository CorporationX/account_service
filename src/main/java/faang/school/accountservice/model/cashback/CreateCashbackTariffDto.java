package faang.school.accountservice.model.cashback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateCashbackTariffDto(
        @NotBlank(message = "Tariff must have name")
        String name,

        @NotEmpty(message = "Operations can't be empty!")
        List<CreateTariffOperationDto> operations,

        @NotEmpty(message = "Merchants can't be empty!")
        List <CreateTariffMerchantDto> merchants
) {
}
