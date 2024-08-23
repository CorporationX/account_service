package faang.school.accountservice.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.Currency;
import faang.school.accountservice.model.OwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotNull
    private OwnerType owner;

    @Positive
    private long ownerId;

    @NotNull
    private AccountType accountType;

    @NotNull
    private Currency currency;

    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.VALID;

    @JsonProperty("accountStatus")
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus != null ? accountStatus : AccountStatus.VALID;
    }
}