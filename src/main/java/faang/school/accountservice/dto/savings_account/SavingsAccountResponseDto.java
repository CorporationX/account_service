package faang.school.accountservice.dto.savings_account;

import faang.school.accountservice.dto.tariff.TariffResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsAccountResponseDto {
    private Long savingsAccountId;
    private TariffResponseDto tariffAndInterestRate;
}