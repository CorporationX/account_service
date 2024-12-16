package faang.school.accountservice.dto.account;

import faang.school.accountservice.model.account.AccountStatus;
import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.Currency;
import faang.school.accountservice.model.owner.OwnerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDto {
    private String accountNumber;
    private Long ownerId;
    private OwnerType ownerType;
    private AccountType accountType;
    private Currency currency;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private Boolean isVerified;
    private String notes;
}
