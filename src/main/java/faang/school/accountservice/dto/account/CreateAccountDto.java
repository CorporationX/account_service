package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAccountDto {

    @NotNull
    @Size(min = 12, max = 20)
    private final String accountNumber;

    @NotNull
    private final Long ownerId;

    @NotNull
    private final Currency currency;

    @NotNull
    private final AccountType accountType;

    private final AccountStatus status = AccountStatus.ACTIVE;
}
