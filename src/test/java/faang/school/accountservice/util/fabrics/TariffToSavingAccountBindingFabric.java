package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.entity.savings_account.SavingsAccount;
import faang.school.accountservice.entity.savings_account.TariffToSavingAccountBinding;
import faang.school.accountservice.entity.tariff.Tariff;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TariffToSavingAccountBindingFabric {
    public static TariffToSavingAccountBinding buildTariffToSavingAccountBinding(Tariff tariff, SavingsAccount savingsAccount) {
        return TariffToSavingAccountBinding.builder()
                .tariff(tariff)
                .savingsAccount(savingsAccount)
                .build();
    }
}
