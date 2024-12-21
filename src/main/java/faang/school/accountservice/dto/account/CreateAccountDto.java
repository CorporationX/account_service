package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.Currency;
import faang.school.accountservice.entity.account.Owner;
import faang.school.accountservice.entity.account.Type;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAccountDto {
    @NotNull
    @NotBlank
    @Size(min = 12, max = 20, message = "accountNumber must have length between 12 and 20 ")
    private String accountNumber;
    @NotNull
    private Owner owner;
    @Min(value = 0, message = "ownerId cant be < 0")
    private long ownerId;
    @NotNull
    private Type type;
    @NotNull
    private Currency currency;
}
