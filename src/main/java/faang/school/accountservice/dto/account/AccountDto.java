package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    private long id;

    private String number;

    private long ownerId;

    private OwnerType ownerType;

    private AccountType accountType;

    private Currency currency;

    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private int version;
}
