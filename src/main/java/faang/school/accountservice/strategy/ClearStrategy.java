package faang.school.accountservice.strategy;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.OperationType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ClearStrategy implements BalanceOperationStrategy {

    @Override
    public boolean isApplicable(OperationType operationType) {
        return operationType == OperationType.CLEAR;
    }

    @Override
    public void apply(Balance balance, BigDecimal amount) {
        if (isApplicable(OperationType.CLEAR)) {
            balance.setActualBalance(balance.getActualBalance().subtract(amount));
        }
    }
}
