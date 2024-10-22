package faang.school.accountservice.model.dto;

import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.model.enums.AccountType;
import faang.school.accountservice.model.enums.Currency;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountDto {
    private Long id;
    private String number;
    private Long project_id;
    private Long userId;
    private AccountType type;
    private Currency currency;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
}
