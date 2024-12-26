package faang.school.accountservice.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceCreateRequest {
    private Long accountId;
    private Long authorisationBalance;
    private Long actualBalance;
}