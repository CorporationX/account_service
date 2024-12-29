package faang.school.accountservice.dto.account.saving;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.tariff.TariffDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccountDto {
    private Long id;
    @NotNull
    private AccountDto account;
    @NotNull
    private TariffDto tariff;

    private LocalDateTime increasedAt;

}
