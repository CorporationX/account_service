package faang.school.accountservice.dto.savingsaccount;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SavingsAccountResponseDto {
    private Long id;
    private String accountId;
    private BigDecimal balance;
    private List<Long> tariffHistory;
    private LocalDateTime lastInterestDate;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
