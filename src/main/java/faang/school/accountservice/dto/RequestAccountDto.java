package faang.school.accountservice.dto;

import faang.school.accountservice.entity.enums.AccountType;
import faang.school.accountservice.entity.enums.Currency;
import faang.school.accountservice.entity.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestAccountDto {

    private final static String ERROR_MESSAGE = "can not be null!";

    @Positive(message = "ownerId must be a positive")
    private long ownerId;

    @NotNull(message = "ownerType " + ERROR_MESSAGE)
    private OwnerType ownerType;

    @NotNull(message = "accountType " + ERROR_MESSAGE)
    private AccountType accountType;

    @NotNull(message = "currency " + ERROR_MESSAGE)
    private Currency currency;
}