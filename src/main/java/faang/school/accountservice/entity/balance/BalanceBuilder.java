package faang.school.accountservice.entity.balance;

import faang.school.accountservice.entity.Account;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class BalanceBuilder {
    public static Balance build(Account account) {
        return Balance.builder()
                .id(UUID.randomUUID())
                .account(account)
                .build();
    }

    public static Balance build(Account account, BigDecimal amount) {
        return Balance.builder()
                .id(UUID.randomUUID())
                .account(account)
                .currentBalance(amount)
                .build();
    }
}
