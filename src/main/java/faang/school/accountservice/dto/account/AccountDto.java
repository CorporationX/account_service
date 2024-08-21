package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    @NotNull
    @Size(min = 12, max = 20, message = "Number must be between 12 and 20 characters")
    private String number;

    @NotBlank
    private OwnerType owner;

    @Positive
    @NotNull
    private long ownerId;

    @NotBlank
    private AccountType accountType;

    @NotBlank
    private AccountStatus accountStatus;
}
