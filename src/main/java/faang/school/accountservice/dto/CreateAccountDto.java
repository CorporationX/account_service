package faang.school.accountservice.dto;

import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateAccountDto {
    @NotNull
    private OwnerType ownerType;

    @NotNull
    @Positive
    private Long ownerId;

    @NotNull
    private AccountType accountType;
}
