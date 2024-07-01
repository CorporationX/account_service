package faang.school.accountservice.dto.account;

import faang.school.accountservice.dto.OwnerDto;
import faang.school.accountservice.model.enums.AccountType;
import faang.school.accountservice.model.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreateDto {

    @NotNull(message = "The owner id cannot be empty")
    private OwnerDto owner;

    @NotNull(message = "The account type cannot be empty")
    private AccountType accountType;

    @NotNull(message = "The currency cannot be empty")
    private Currency currency;
}
