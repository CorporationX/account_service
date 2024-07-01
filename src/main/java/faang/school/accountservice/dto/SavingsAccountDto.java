package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountDto {
    private Long id;

    private Long accountNumber;

    private Long currentTariffId;

    private BigDecimal currentTariffRate;

    private LocalDateTime lastInterestCalculatedDate;

    private BigDecimal currentBalance;

    private BigDecimal authorizedBalance;
}
