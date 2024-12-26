package faang.school.accountservice.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDto {
    private Long id;
    private Long accountId;
    private Long authorisationBalance;
    private Long actualBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
