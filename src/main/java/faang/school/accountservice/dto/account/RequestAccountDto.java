package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestAccountDto {

    @NotNull(message = "OwnerType cannot be null")
    private OwnerType ownerType;

    @NotNull(message = "AccountType cannot be null")
    private AccountType accountType;

    @NotNull(message = "Currency cannot be null")
    private Currency currency;
}
