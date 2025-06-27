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
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateProjectRequestDto {
    @NotNull(message = "projectId is mandatory")
    private long projectId;

    @NotNull(message = "currencyId is mandatory")
    private UUID currencyId;

    @NotNull(message = "type is mandatory")
    private AccountType type;
}
