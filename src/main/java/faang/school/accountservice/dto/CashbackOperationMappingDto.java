package faang.school.accountservice.dto;

import faang.school.accountservice.enums.OperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashbackOperationMappingDto {
    @Null
    private Long id;

    @Null
    private Long cashbackTariffId;

    @NotNull
    private OperationType operationType;

    @NotNull
    private BigDecimal percentage;
}

