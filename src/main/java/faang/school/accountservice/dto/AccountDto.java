package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {
    @NotNull
    OwnerType ownerType;
    @NotNull
    Long ownerId;
    @NotNull
    AccountType accountType;
    @NotNull
    Currency currency;

//    BigDecimal balance;
}
