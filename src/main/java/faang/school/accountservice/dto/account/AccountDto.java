package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.enums.AccountStatus;
import faang.school.accountservice.entity.account.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private Long id;
    private String paymentNumber;
    @NotNull
    private AccountType type;
    @NotNull
    private Currency currency;
    @NotNull
    private AccountStatus status;
}
