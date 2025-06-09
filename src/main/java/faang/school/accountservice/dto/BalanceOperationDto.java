package faang.school.accountservice.dto;

import faang.school.accountservice.enums.OperationType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class BalanceOperationDto {

    @NotNull(message = "Amount can't be null")
    @Positive(message = "Amount can't be negative")
    @Digits(integer = 16, fraction = 4, message = "Amount must have at most 4 digits after the decimal point")
    private BigDecimal amount;

    @NotNull(message = "Operation type can't be null")
    private OperationType operationType;
}