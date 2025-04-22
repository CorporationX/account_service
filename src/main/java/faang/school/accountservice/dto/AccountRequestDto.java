package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDto {
    @NotNull(message = "Owner ID can't be null")
    private Long ownerId;

    @NotNull(message = "Owner type can't be null")
    private OwnerTypeDto ownerType;

    @NotNull(message = "Currency can't be null")
    private CurrencyDto currency;

    @NotNull(message = "Account type can't be null")
    private AccountTypeDto accountType;

    private String description;
}
