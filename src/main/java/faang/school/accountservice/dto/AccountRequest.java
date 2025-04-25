package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    @NotNull(message = "Account type must not be null")
    private AccountType type;

    @NotNull(message = "Owner type must not be null")
    private OwnerType ownerType;

    @NotNull(message = "Currency must not be null")
    private Currency currency;

    @NotNull(message = "Owner ID must not be null")
    private Long ownerId;

    private LocalDateTime scheduledAt;
}


