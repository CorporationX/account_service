package faang.school.accountservice.dto;

import faang.school.accountservice.entity.enums.AccountType;
import faang.school.accountservice.entity.enums.Currency;
import faang.school.accountservice.entity.enums.OwnerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestAccountDto {
    private long ownerId;
    private OwnerType ownerType;
    private AccountType accountType;
    private Currency currency;
}