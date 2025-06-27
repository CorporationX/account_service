package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateUserRequestDto {
    @NotNull(message = "userId is mandatory")
    private long userId;

    @NotNull(message = "currency is mandatory")
    private UUID currencyId;

    @NotNull(message = "type is mandatory")
    private AccountType type;
}
