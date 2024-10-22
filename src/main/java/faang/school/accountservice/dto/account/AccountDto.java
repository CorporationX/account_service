package faang.school.accountservice.dto.account;

import faang.school.accountservice.dto.owner.OwnerDto;
import faang.school.accountservice.dto.type.TypeDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private String number;
    private OwnerDto owner;
    private TypeDto type;
    private Currency currency;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
}
