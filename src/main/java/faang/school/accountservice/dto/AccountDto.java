package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccountDto {

    private Long id;

    private String accountNumber;

    @NotNull
    @Min(1)
    private Long ownerId;

    @NotNull
    private OwnerType ownerType;

    @NotNull
    private AccountType accountType;

    @NotNull
    private Currency currency;

    private AccountStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;
}
