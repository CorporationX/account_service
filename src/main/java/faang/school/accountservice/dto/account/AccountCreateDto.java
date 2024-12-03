package faang.school.accountservice.dto.account;

import faang.school.accountservice.dto.owner.OwnerDto;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateDto {

    @NotNull
    private Long id;

    @NotNull
    private OwnerDto ownerName;

    @NotNull
    private AccountTypeDto accountType;

    @NotNull
    private Currency currency;
}
