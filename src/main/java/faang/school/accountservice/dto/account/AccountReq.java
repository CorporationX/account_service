package faang.school.accountservice.dto.account;

import faang.school.accountservice.annotation.ValueOfEnum;
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
public class AccountReq {
    @NotNull
    @Size(min = 12, max = 20)
    private String accountNumber;
    private Long userOwnerId;
    private Long projectOwnerId;
    @ValueOfEnum(enumClass = Currency.class)
    private String currency;
    @ValueOfEnum(enumClass = AccountType.class)
    private String accountType;
}
