package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.savings_account.SavingsAccount;
import faang.school.accountservice.entity.tariff.Tariff;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.UUID;

@UtilityClass
public class SavingsAccountFabric {
    public static SavingsAccount buildSavingsAccount() {
        return SavingsAccount.builder()
                .tariffToSavingAccountBindings(new ArrayList<>())
                .build();
    }

    public static SavingsAccount buildSavingsAccount(UUID id) {
        return SavingsAccount.builder()
                .id(id)
                .build();
    }

    public static SavingsAccount buildSavingsAccount(UUID id, Account account) {
        return SavingsAccount.builder()
                .id(id)
                .account(account)
                .tariffToSavingAccountBindings(new ArrayList<>())
                .build();
    }

    public static SavingsAccount buildSavingsAccount(UUID id, Tariff tariff) {
        return SavingsAccount.builder()
                .id(id)
                .tariffToSavingAccountBindings(new ArrayList<>())
                .build();
    }
}
