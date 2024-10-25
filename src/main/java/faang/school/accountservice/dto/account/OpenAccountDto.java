package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.account.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAccountDto {
    private Long userId;
    private Long projectId;
    @NotNull(message = "Type cannot be null")
    private AccountType type;
    @NotNull(message = "Currency cannot be null")
    private Currency currency;
}
