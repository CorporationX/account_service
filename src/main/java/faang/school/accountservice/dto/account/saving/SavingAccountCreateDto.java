package faang.school.accountservice.dto.account.saving;

import faang.school.accountservice.dto.account.AccountDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountCreateDto {
    @NotNull
    private AccountDto account;
    @NotNull
    private Long tariffId;


}
