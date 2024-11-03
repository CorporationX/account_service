package faang.school.accountservice.service.balance.changebalance;

import faang.school.accountservice.enums.ChangeBalanceType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BalanceChangeRegistry {

    private final Map<ChangeBalanceType, BalanceChanger> changes = new HashMap<>();

    public void registerBalanceChange(BalanceChanger change) {
        changes.put(change.getChangeBalanceType(), change);
    }

    public BalanceChanger getBalanceChange(ChangeBalanceType changeBalanceType) {
        BalanceChanger balanceChanger = changes.get(changeBalanceType);
        if (balanceChanger == null) {
            throw new IllegalArgumentException("Unknown change type: " + changeBalanceType);
        }
        return balanceChanger;
    }
}
