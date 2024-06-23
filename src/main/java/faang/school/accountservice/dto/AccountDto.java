package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.enums.account.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private Long id;

    private BigInteger number;

    private Long ownerId;

    @NotNull(message = "account type can't be null")
    private AccountType accountType;

    @NotNull(message = "account currency can't be blank")
    private Currency currency;

    private AccountStatus accountStatus;

    private BigDecimal balance;
}