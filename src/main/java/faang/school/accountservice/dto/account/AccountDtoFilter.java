package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDtoFilter {
    private List<Long> ownerIds;
    private OwnerType ownerType;
    private List<AccountType> accountTypes;
    private List<Currency> currencies;
    private List<AccountStatus> statuses;
    private LocalDateTime createdAtGt;
    private LocalDateTime createdAtLt;
    private LocalDateTime closedAtGt;
    private LocalDateTime closedAtLt;
    private Boolean isVerified;
    private String notes;
}
