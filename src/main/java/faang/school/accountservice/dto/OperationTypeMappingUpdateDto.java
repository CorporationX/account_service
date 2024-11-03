package faang.school.accountservice.dto;

import faang.school.accountservice.enums.OperationType;
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
public class OperationTypeMappingUpdateDto {

    @NotNull(message = "Id cannot be null")
    @Positive(message = "Id must be a positive number")
    private Long id;

    @NotNull(message = "Operation type cannot be null")
    private OperationType operationType;

    @DecimalMin(value = "0.01", message = "Percentage must be at least 0.01")
    @DecimalMax(value = "100.00", message = "Percentage cannot exceed 100.00")
    private BigDecimal percentage;

    @Positive(message = "Cashback tariff ID must be a positive number")
    private Long cashbackTariffId;
}


