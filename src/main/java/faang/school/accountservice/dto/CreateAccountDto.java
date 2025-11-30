package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountDto(
        @NotNull
        BigDecimal balance,
        @NotNull
        @JsonProperty("owner_id")
        Long ownerId,
        @NotNull
        @JsonProperty("owner_type")
        OwnerType ownerType,
        @NotNull
        @JsonProperty("account_type")
        AccountType accountType,
        Currency currency
) {
}