package faang.school.accountservice.dto.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record TariffDto(
        Long id,
        @NotBlank(message = "Tariff name cannot be empty")
        String name,
        @DecimalMin(value = "0.0", inclusive = false, message = "Current rate must be greater than 0")
        double currentRate
) {
}
