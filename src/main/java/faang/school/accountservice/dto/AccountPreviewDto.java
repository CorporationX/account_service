package faang.school.accountservice.dto;

import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.model.AccountType;
import faang.school.accountservice.model.OwnerType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountPreviewDto {
    private Long id;
    private OwnerType ownerType;
    private Long ownerId;
    private AccountType accountType;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
}
