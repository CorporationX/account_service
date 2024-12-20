package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountDto(
        @NotNull(message = "Project owner type should not be empty")
        AccountOwnerType ownerType,

//        @NotNull(message = "Owner id cannot be null")
//        @Positive(message = "Owner id should be a positive number")
//        Long ownerId,

        @NotBlank(message = "Owner name cannot be empty")
        String ownerName,

        @NotNull(message = "Account type cannot be empty")
        AccountType accountType,

        @NotNull(message = "Currency cannot be empty")
        Currency currency
        ) {
}
