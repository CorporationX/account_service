package faang.school.accountservice.model.savings_account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountChangeRecord {
    private Long userId;
    private Long oldTariffId;
    private Long newTariffId;
}