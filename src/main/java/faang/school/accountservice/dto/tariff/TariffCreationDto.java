package faang.school.accountservice.dto.tariff;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TariffCreationDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Initial rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be positive")
    private BigDecimal initRate;
}
