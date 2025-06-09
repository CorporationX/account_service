package faang.school.accountservice.strategy;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.OperationType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepositStrategy implements BalanceOperationStrategy {

    @Override
    public boolean isApplicable(OperationType operationType) {
        return operationType == OperationType.DEPOSIT;
    }

    @Override
    public void apply(Balance balance, BigDecimal amount) {
        if (isApplicable(OperationType.DEPOSIT)) {
            balance.setAuthorizedBalance(balance.getAuthorizedBalance().add(amount));
            balance.setActualBalance(balance.getActualBalance().add(amount));
        }
    }
}
