package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountCreateDto(

        @NotNull
        @Positive
        Long ownerId,

        @NotNull
        OwnerType ownerType,

        @NotNull
        AccountType accountType,

        @NotNull
        Currency currency) {
}