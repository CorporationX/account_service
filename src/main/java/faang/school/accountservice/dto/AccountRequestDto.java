package faang.school.accountservice.dto;

import faang.school.accountservice.enums.OwnerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDto {
    private Long userId;
    private OwnerType ownerType;
    private CurrencyDto currency;
    private AccountTypeDto accountType;
}
