package faang.school.accountservice.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {
    private Long id;
    private Long accountId;
    private BigDecimal authorisationBalance;
    private BigDecimal actualBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
