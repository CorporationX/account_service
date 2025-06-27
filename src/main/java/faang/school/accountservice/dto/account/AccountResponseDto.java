package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.AccountOwnerType;
import faang.school.accountservice.entity.account.AccountStatus;
import faang.school.accountservice.entity.account.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDto {
    private UUID id;
    private String number;
    private Long userId;
    private Long projectId;
    private AccountOwnerType ownerType;
    private AccountType type;
    private UUID currencyId;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closeAt;
}
