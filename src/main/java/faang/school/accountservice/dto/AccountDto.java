package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@Builder
public class AccountDto {
    private long id;
    private String number;
    @NonNull
    private OwnerType ownerType;
    private Long ownerProjectId;
    private Long ownerUserId;
    @NonNull
    private AccountType accountType;
    @NonNull
    private Currency currency;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;

}
