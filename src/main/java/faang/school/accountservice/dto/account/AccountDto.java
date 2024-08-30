package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    @Positive
    private long id;

    @NotNull
    @Size(min = 12, max = 20)
    private String number;

    @Positive
    private long ownerId;

    @NotNull
    private OwnerType ownerType;

    @NotNull
    private AccountType accountType;

    @NotNull
    private Currency currency;

    @NotNull
    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @NotNull
    private int version;
}
