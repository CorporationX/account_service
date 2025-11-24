package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        @JsonProperty("id")
        Long id,

        @JsonProperty("account_number")
        String accountNumber,

        @JsonProperty("owner_id")
        Long ownerId,

        @JsonProperty("owner_type")
        OwnerType ownerType,

        @JsonProperty("account_type")
        AccountType accountType,

        @JsonProperty("currency")
        Currency currency,

        @JsonProperty("status")
        AccountStatus status,

        @JsonProperty("balance")
        BigDecimal balance,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        LocalDateTime updatedAt,

        @JsonProperty("closed_at")
        LocalDateTime closedAt,

        @JsonProperty("version")
        Long version
) {
}
