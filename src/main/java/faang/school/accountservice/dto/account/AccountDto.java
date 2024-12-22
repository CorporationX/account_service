package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.Currency;
import faang.school.accountservice.entity.account.OwnerType;
import faang.school.accountservice.entity.account.Status;
import faang.school.accountservice.entity.account.Type;
import lombok.Data;

@Data
public class AccountDto {
    private Long id;
    private OwnerType ownerType;
    private long ownerId;
    private Type type;
    private Currency currency;
    private Status status;
}
