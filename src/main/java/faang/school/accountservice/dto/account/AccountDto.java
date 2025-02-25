package faang.school.accountservice.dto.account;

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
        String ownerType,

        @NotNull(message = "Currency can't be null")
        String currency,

        @NotNull(message = "Account type can't be null")
        String type,

        String status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        LocalDateTime closedDate,

        long version
) {
}
