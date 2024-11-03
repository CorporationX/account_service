package faang.school.accountservice.service.balance.changebalance;

import faang.school.accountservice.enums.ChangeBalanceType;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.service.balance.operation.Operation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AuthorizationBalanceChanger implements BalanceChanger {

    @Override
    public Balance processBalance(Balance balance, BigDecimal amount, Operation operation) {
        BigDecimal authBalance = operation.calculateBalance(balance.getAuthBalance(), amount);
        if (authBalance.compareTo(balance.getActualBalance()) > 0) {
            throw new InsufficientFundsException("Insufficient funds to auth balance: " + authBalance);
        }
        balance.setAuthBalance(authBalance);
        return balance;
    }

    @Override
    public ChangeBalanceType getChangeBalanceType() {
        return ChangeBalanceType.AUTHORIZATION;
    }
}
