package faang.school.accountservice.dto.savings_account;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.enumeration.TariffType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountResponseDto {
    private Long id;
    private String number;
    private String owner;
    private Currency currency;
    private Long amount;
    private TariffType tariffType;
    private Double rate;
}
