package faang.school.accountservice.service.balance;

import faang.school.accountservice.enums.BalanceChangeType;
import faang.school.accountservice.exception.InsufficientFundsException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WithdrawalChangeType implements BalanceChange {

    @Override
    public BigDecimal calculateBalance(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds to withdraw, balance: " +
                    balance + ", amount: " + amount);
        }
        return balance.subtract(amount);
    }

    @Override
    public BalanceChangeType getChangeType() {
        return BalanceChangeType.WITHDRAWAL;
    }
}
