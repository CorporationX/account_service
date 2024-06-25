package faang.school.accountservice.dto.account;

import faang.school.accountservice.dto.OwnerDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotBlank;
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
    @Size(min = 12, max = 20, message = "The account number length must be from 12 to 20 characters.")
    @NotBlank(message = "The account number cannot be empty")
    private String number;

    @NotNull(message = "The owner id cannot be empty")
    private OwnerDto owner;

    @NotNull(message = "The account type cannot be empty")
    private AccountType accountType;

    @NotNull(message = "The currency cannot be empty")
    private Currency currency;
}