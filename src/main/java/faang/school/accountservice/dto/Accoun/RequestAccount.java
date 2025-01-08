package faang.school.accountservice.dto.Accoun;

import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.entity.Owner;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestAccount {
    @NotNull(message = "This is Id null")
    long user_owner_id;

    @NotNull(message = "This is type null")
    AccountType type;

    Owner owner_account;
}
