package faang.school.accountservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record SavingsAccountDto(
        Long id,
        Long accountId,
        BigDecimal balance,
        List<Long> tariffHistory,
        LocalDate lastInterestCalculationDate
) {
}