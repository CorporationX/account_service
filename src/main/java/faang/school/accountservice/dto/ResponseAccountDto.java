package faang.school.accountservice.dto;

import faang.school.accountservice.entity.enums.AccountType;
import faang.school.accountservice.entity.enums.OwnerType;
import faang.school.accountservice.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseAccountDto {
    private long id;
    private String number;
    private long ownerId;
    private OwnerType ownerType;
    private AccountType accountType;
    private Status status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime closedAt;
}