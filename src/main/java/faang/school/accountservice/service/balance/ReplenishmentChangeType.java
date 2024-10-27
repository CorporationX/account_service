package faang.school.accountservice.service.balance;

import faang.school.accountservice.enums.BalanceChangeType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ReplenishmentChangeType implements BalanceChange {

    @Override
    public BigDecimal calculateBalance(BigDecimal balance, BigDecimal amount) {
        return balance.add(amount);
    }

    @Override
    public BalanceChangeType getChangeType() {
        return BalanceChangeType.REPLENISHMENT;
    }
}
