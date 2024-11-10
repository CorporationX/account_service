package faang.school.accountservice.service.balance.changebalance;

import faang.school.accountservice.enums.ChangeBalanceType;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.service.balance.operation.Operation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ActualBalanceChanger implements BalanceChanger {

    @Override
    public Balance processBalance(Balance balance, BigDecimal amount, Operation operation) {
        BigDecimal actualBalance = operation.calculateBalance(balance.getActualBalance(), amount);

        if (actualBalance.compareTo(balance.getAuthBalance()) < 0) {
            throw new InsufficientFundsException("Insufficient funds to change balance, auth balance: "
                    + balance.getAuthBalance() + ", balance: " + balance.getActualBalance());
        }
        balance.setActualBalance(actualBalance);
        return balance;
    }

    @Override
    public ChangeBalanceType getChangeBalanceType() {
        return ChangeBalanceType.ACTUAL;
    }
}
