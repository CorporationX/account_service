package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigInteger;

@Data
public class AccountDto {
    private long id;
    private BigInteger accountNumber;
    private long balanceId;
    @NotNull
    private long accountOwnerId;
    @NotNull
    private OwnerType ownerType;
    @NotNull
    private AccountType accountType;
    @NotNull
    private Currency currency;
    private AccountStatus accountStatus;
}
