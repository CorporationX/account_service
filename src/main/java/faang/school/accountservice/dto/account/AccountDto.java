package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AccountDto(

        long id,

        String number,

        @NotNull(message = "Owner id can't be null")
        @Min(value = 1, message = "Owner id should be more than 0")
        long ownerId,

        @NotNull(message = "Owner type can't be null")
        OwnerType ownerType,

        @NotNull(message = "Currency can't be null")
        Currency currency,

        @NotNull(message = "Account type can't be null")
        AccountType type,

        AccountStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        LocalDateTime closedDate
) {
}
