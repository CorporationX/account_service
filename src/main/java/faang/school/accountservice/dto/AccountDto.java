package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {

    private long id;

    @NotNull(message = "Owner must not be null")
    private OwnerDto owner;

    private BalanceDto balance;

    @Size(min = 12, max = 20, message = "account number length should be between 12 and 20")
    private String number;

    @NotNull(message = "Account type must not be null")
    private AccountType type;

    private Status status;

    @NotNull(message = "Currency type must not be null")
    private Currency currency;
}