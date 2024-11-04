package faang.school.accountservice.dto.account;


import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.account.AccountStatus;
import faang.school.accountservice.model.owner.OwnerType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private UUID id;

    private String accountNumber;

    @NotNull(message = "\"externalOwnerId\" cannot be NULL")
    private Long externalId;

    @NotNull(message = "\"ownerType\" cannot be NULL")
    private OwnerType ownerType;

    @NotNull(message = "\"accountType\" cannot be NULL")
    private AccountNumberType accountType;

    @NotNull(message = "\"currency\" cannot be NULL")
    private Currency currency;

    private AccountStatus accountStatus;
}
