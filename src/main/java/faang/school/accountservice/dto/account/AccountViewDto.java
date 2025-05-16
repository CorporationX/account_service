package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountViewDto {
    private String accountNumber;
    private OwnerType ownerType;
    private Long ownerId;
    private AccountType accountType;
    private Currency currency;
    private AccountStatus accountStatus;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant closedAt;
    private Integer version;
    private Long balanceId;
}