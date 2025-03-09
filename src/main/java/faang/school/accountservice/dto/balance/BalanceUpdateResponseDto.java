package faang.school.accountservice.dto.balance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BalanceUpdateResponseDto {
    private Long id;
    private String accountNumber;
    private BigDecimal authorisationBalance;
    private BigDecimal factualBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
