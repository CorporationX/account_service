package faang.school.accountservice.util;

import faang.school.accountservice.entity.Balance;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class BalanceFabrics {

    public static Balance buildBalance(UUID id, double currentBalance, double authBalance) {
        return Balance.builder()
                .id(id)
                .currentBalance(BigDecimal.valueOf(currentBalance))
                .authBalance(BigDecimal.valueOf(authBalance))
                .build();
    }

    public static Balance buildBalance(UUID id) {
        return Balance.builder()
                .id(id)
                .build();
    }
}

