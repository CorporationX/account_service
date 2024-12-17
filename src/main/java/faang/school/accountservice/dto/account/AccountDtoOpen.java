package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.owner.OwnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDtoOpen {
    @NotBlank
    @Pattern(regexp = "^[0-9]{12,20}$", message = "Account number must contain only numbers and be between 12 and 20 digits long.")
    private String accountNumber;

    @NotNull
    @Positive
    private Long ownerId;

    @NotNull
    private OwnerType ownerType;

    @NotNull
    private AccountType accountType;

    @NotNull
    private Currency currency;

    @Size(max=4096, message = "Notes cannot be longer than 4096 characters.")
    private String notes;
}
