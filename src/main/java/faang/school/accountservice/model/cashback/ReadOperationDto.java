package faang.school.accountservice.model.cashback;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReadOperationDto(
        Long id,
        String operationType,
        BigDecimal percentage
) {
}
