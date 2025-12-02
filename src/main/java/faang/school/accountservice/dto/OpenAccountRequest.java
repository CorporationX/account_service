package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;

public record OpenAccountRequest(
    @JsonProperty(value = "owner_id", required = true)
    @NotNull(message = "Owner ID is required")
    Long ownerId,

    @JsonProperty(value = "owner_type", required = true)
    @NotNull(message = "Owner type is required")
    OwnerType ownerType,

    @JsonProperty(value = "account_type", required = true)
    @NotNull(message = "Account type is required")
    AccountType accountType,

    @JsonProperty(value = "currency", required = true)
    @NotNull(message = "Currency is required")
    Currency currency
) {
}


