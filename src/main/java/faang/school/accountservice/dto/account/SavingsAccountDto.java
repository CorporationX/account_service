package faang.school.accountservice.dto.account;

import faang.school.accountservice.dto.tariff.TariffDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsAccountDto {
    private Long id;
    private String number;
    private Long accountId;
    private TariffDto tariffDto;
}
