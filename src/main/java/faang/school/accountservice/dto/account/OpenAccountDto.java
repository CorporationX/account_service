package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.OwnerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAccountDto {

    private String number;

    private long ownerId;

    private OwnerType ownerType;

    private AccountType accountType;

    private Currency currency;
}
