package faang.school.accountservice.service.balance.operation;

import faang.school.accountservice.enums.OperationType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ReplenishmentOperation implements Operation {

    @Override
    public BigDecimal calculateBalance(BigDecimal balance, BigDecimal amount) {
        return balance.add(amount);
    }

    @Override
    public OperationType getChangeType() {
        return OperationType.REPLENISHMENT;
    }
}
