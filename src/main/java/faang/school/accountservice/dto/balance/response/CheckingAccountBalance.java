package faang.school.accountservice.dto.balance.response;

import faang.school.accountservice.enums.pending.AccountBalanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CheckingAccountBalance {
    private UUID operationId;
    private UUID sourceAccountId;
    private AccountBalanceStatus status;
}
