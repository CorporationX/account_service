package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class UpdateAccountDto {
    @NonNull
    private OwnerType ownerType;
    private Long ownerProjectId;
    private Long ownerUserId;
    @NonNull
    private Currency currency;
}
