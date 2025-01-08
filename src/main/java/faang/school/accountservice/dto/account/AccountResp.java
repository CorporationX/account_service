package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResp {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private Currency currency;
    private AccountStatus accountStatus;
}
