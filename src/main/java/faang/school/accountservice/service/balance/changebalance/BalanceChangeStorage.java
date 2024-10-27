package faang.school.accountservice.service.balance.changebalance;

import faang.school.accountservice.enums.ChangeBalanceType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BalanceChangeStorage {

    private final Map<ChangeBalanceType, BalanceChange> changes = new HashMap<>();

    public void registerBalanceChange(BalanceChange change) {
        changes.put(change.getChangeBalanceType(), change);
    }

    public BalanceChange getBalanceChange(ChangeBalanceType changeBalanceType) {
        BalanceChange balanceChange = changes.get(changeBalanceType);
        if (balanceChange == null) {
            throw new IllegalArgumentException("Unknown change type: " + changeBalanceType);
        }
        return balanceChange;
    }
}
