package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAccountDto {

    @Pattern(regexp = "^[0-9]{12,20}$", message = "Account number must be 12-20 digits")
    private String number;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotNull(message = "Owner type is required")
    private OwnerType ownerType;

    @NotNull(message = "Account type is required")
    private AccountType type;

    @NotNull(message = "Currency is required")
    private Currency currency;
}

