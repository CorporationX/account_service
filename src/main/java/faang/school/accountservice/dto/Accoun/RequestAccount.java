package faang.school.accountservice.dto.Accoun;

import faang.school.accountservice.entity.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.Owner;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestAccount {

    Long userOwnerId;

    Long projectOwnerId;

    @NotNull(message = "This is type null")
    AccountType type;

    @NotNull(message = "This is owner null")
    Owner ownerAccount;

    @NotNull(message = "This is currency null")
    Currency currency;

    @NotNull(message = "This is version null")
    long version;
}
