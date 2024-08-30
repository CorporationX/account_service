package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAccountDto {

    @NotNull
    @Size(min = 12, max = 20)
    private String number;

    @Positive
    private long ownerId;

    @NotNull
    private OwnerType ownerType;

    @NotNull
    private AccountType accountType;

    @NotNull
    private Currency currency;
}
