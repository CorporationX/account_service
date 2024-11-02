package faang.school.accountservice.model.cashback;

import faang.school.accountservice.model.enums.Operation;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OperationDto(
        Long id,
        Operation operationType,
        BigDecimal percentage
) {
}
