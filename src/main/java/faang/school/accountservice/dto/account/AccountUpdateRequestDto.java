package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.AccountStatus;
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
public class AccountUpdateRequestDto {
    @NotNull(message = "id is mandatory")
    private UUID id;
    @NotNull(message = "status is mandatory")
    private AccountStatus status;
}
