package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
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
public class AccountDtoResponse {
    private Long id;
    private String accountNumber;
    private Long ownerId;
    private OwnerType ownerType;
    private AccountType accountType;
    private Currency currency;
    private AccountStatus status;
    private LocalDateTime createdAt;
}
