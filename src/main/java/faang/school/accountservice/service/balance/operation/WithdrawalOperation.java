package faang.school.accountservice.service.balance.operation;

import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.exception.InsufficientFundsException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WithdrawalOperation implements Operation {

    @Override
    public BigDecimal calculateBalance(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds to withdraw, balance: " +
                    balance + ", amount: " + amount);
        }
        return balance.subtract(amount);
    }

    @Override
    public OperationType getChangeType() {
        return OperationType.WITHDRAWAL;
    }
}
