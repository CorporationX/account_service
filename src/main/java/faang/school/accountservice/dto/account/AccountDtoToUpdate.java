package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDtoToUpdate {

    private AccountType accountType;

    private Currency currency;
}
