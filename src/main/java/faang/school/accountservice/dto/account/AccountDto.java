package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.Currency;
import faang.school.accountservice.entity.account.Owner;
import faang.school.accountservice.entity.account.Status;
import faang.school.accountservice.entity.account.Type;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountDto {
    private Long id;
    @NotNull
    private Owner owner;
    @Min(value = 0, message = "ownerId cant be < 0")
    private long ownerId;
    @NotNull
    private Type type;
    @NotNull
    private Currency currency;
    private Status status;
}
