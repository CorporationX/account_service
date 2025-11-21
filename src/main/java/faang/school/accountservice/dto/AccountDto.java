package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountDto(
        Long id,
        @JsonProperty("account_number")
        String accountNumber,
        BigDecimal balance,
        OwnerDto owner,
        @JsonProperty("account_type")
        AccountType accountType,
        Currency currency,
        @JsonProperty("account_status")
        AccountStatus accountStatus,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("updated_at")
        LocalDateTime updatedAt,
        @JsonProperty("closed_at")
        LocalDateTime closedAt,
        @JsonProperty("account_version")
        Long accountVersion
) {
}