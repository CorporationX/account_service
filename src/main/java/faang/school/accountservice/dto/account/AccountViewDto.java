package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.dto.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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
    private BigDecimal balance;
}