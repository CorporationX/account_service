package faang.school.accountservice.dto.account;

import faang.school.accountservice.dto.owner.OwnerDto;
import faang.school.accountservice.dto.type.TypeDto;
import faang.school.accountservice.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "POJO for account creation")
public class AccountCreateDto {

    @NotNull
    private OwnerDto owner;

    @NotNull
    private TypeDto type;

    @NotNull
    private Currency currency;
}
