package faang.school.accountservice.dto;

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
    OwnerType ownerType;
    @NotNull
    Long ownerId;
    @NotNull
    AccountType accountType;
    @NotNull
    Currency currency;

    BigDecimal balance;
    @Size(max = 512, message = "Description should contain at max 512 chars")
    String description;
}
