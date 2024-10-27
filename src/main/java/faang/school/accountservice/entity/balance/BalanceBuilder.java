package faang.school.accountservice.entity.balance;

import faang.school.accountservice.entity.Account;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class BalanceBuilder {
    public static Balance build(Account account) {
        return Balance.builder()
                .id(UUID.randomUUID())
                .account(account)
                .build();
    }
}
