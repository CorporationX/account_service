package faang.school.accountservice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantMappingDto {

    private Long id;

    @NotNull(message = "Merchant ID cannot be null")
    @Positive
    private Long merchantId;

    @NotNull(message = "Percentage cannot be null")
    @DecimalMin(value = "0.01", message = "Percentage must be at least 0.01")
    @DecimalMax(value = "100.00", message = "Percentage cannot exceed 100.00")
    private BigDecimal percentage;
}

