package faang.school.accountservice.dto.savingsAccounts;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SavingsAccountReadDto {
    private Long id;
    private Long accountId;
    private BigDecimal balance;
    private String tariffHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
