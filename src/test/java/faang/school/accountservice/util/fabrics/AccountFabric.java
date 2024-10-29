package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.balance.Balance;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class AccountFabric {
    public static Account buildAccount() {
        return Account.builder()
                .build();
    }

    public static Account buildAccount(UUID id) {
        return Account.builder()
                .id(id)
                .build();
    }

    public static Account buildAccount(Balance balance) {
        return Account.builder()
                .balance(balance)
                .build();
    }
}
