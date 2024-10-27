package faang.school.accountservice.service.balance;

import faang.school.accountservice.enums.BalanceChangeType;
import faang.school.accountservice.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public interface BalanceChange {
    BigDecimal calculateBalance(BigDecimal balance, BigDecimal amount);

    BalanceChangeType getChangeType();

    @Autowired
    default void register(BalanceService balanceService) {
        balanceService.registerBalanceChange(getChangeType(), this);
    }
}