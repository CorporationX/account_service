package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDto {
    @NotNull
    @Size(min = 12, max = 20)
    String number;
    @NotNull
    OwnerType ownerType;
    @NotNull
    Long ownerId;
    @NotNull
    AccountType accountType;
    @NotNull
    Currency currency;
    @NotNull
    AccountStatus status;

    BigDecimal balance;
    @Size(max = 512, message = "Description should contain at max 512 chars")
    String description;
}
