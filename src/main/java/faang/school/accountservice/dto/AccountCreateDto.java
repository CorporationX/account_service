package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountCreateDto(
        @Schema(
                description = "Уникальный идентификатор владельца счета",
                example = "12345",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        @Positive
        Long ownerId,

        @Schema(
                description = "Тип владельца счета",
                example = "USER",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        OwnerType ownerType,

        @Schema(
                description = "Тип счета",
                example = "PERSONAL",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        AccountType accountType,

        @Schema(
                description = "Валюта счета",
                example = "USD",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Currency currency) {

}
