package faang.school.accountservice.entity.savings_account;

import faang.school.accountservice.entity.tariff.Tariff;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SavingsAccountTariffBuilder {
    public static TariffToSavingAccountBinding build(Tariff tariff, SavingsAccount savingsAccount) {
        return TariffToSavingAccountBinding.builder()
                .tariff(tariff)
                .savingsAccount(savingsAccount)
                .build();
    }
}
