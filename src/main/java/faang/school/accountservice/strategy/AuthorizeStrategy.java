package faang.school.accountservice.strategy;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.OperationType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AuthorizeStrategy implements BalanceOperationStrategy {

    @Override
    public boolean isApplicable(OperationType operationType) {
        return operationType == OperationType.AUTHORIZE;
    }

    @Override
    public void apply(Balance balance, BigDecimal amount) {
        if (isApplicable(OperationType.AUTHORIZE)) {
            balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));
        }
    }
}
