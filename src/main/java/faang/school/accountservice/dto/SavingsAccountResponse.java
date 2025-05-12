package faang.school.accountservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SavingsAccountResponse {

    private final String accountNumber;
    private final BigDecimal balance;
    private final LocalDateTime lastInterestAccrualAt;
    private final String activeTariff;
    private String activeTariffRate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
