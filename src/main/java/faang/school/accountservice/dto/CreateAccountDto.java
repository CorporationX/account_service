package faang.school.accountservice.dto;

import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountDto {
    @NotNull
    private OwnerType ownerType;

    @NotNull
    @Positive
    private Long ownerId;

    @NotNull
    private AccountType accountType;

    @NotNull
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters long")
    private String currency;
}
