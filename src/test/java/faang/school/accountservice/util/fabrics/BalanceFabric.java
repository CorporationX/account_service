package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.balance.Balance;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class BalanceFabric {
    public static Balance buildBalance() {
        return Balance.builder()
                .build();
    }

    public static Balance buildBalance(Account account) {
        return Balance.builder()
                .account(account)
                .build();
    }

    public static Balance buildBalance(UUID id) {
        return Balance.builder()
                .id(id)
                .build();
    }

    public static Balance buildBalance(UUID id, double currentBalance, double authBalance) {
        return Balance.builder()
                .id(id)
                .currentBalance(BigDecimal.valueOf(currentBalance))
                .authBalance(BigDecimal.valueOf(authBalance))
                .build();
    }
}
