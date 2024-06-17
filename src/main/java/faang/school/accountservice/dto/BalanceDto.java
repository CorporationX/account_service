package faang.school.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BalanceDto {

    private long id;
    private long accountId;
    private long authorizedBalance;
    private long actual_balance;
}