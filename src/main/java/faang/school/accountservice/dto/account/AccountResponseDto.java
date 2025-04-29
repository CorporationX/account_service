package faang.school.accountservice.dto.account;

import faang.school.accountservice.dto.CurrencyDto;
import faang.school.accountservice.dto.OwnerTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDto {
    private String accountNumber;
    private Long ownerId;
    private OwnerTypeDto ownerType;
    private AccountStatusDto accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
    private CurrencyDto currency;
    private AccountTypeDto accountType;
    private String description;
}
