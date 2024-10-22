package faang.school.accountservice.dto;

import faang.school.accountservice.annotation.OneHolderAccount;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@OneHolderAccount
public class AccountDto {

    private Long id;

    @NotNull
    @Size(min = 12, max = 20)
    private String number;

    private Long holderUserId;

    private Long holderProjectId;

    @NotNull
    private AccountType type;

    @NotNull
    private Currency currency;

    @NotNull
    private AccountStatus status;
}

