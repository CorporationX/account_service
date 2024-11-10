package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.payment.Currency;
import faang.school.accountservice.enums.account.AccountStatus;
import faang.school.accountservice.enums.account.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponseDto {
    private UUID id;
    private String number;
    private Long userId;
    private Long projectId;
    private AccountType type;
    private Currency currency;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
}
