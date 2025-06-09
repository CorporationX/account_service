package faang.school.accountservice.strategy;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.OperationType;

import java.math.BigDecimal;

public interface BalanceOperationStrategy {
    boolean isApplicable(OperationType operationType);
    void apply(Balance balance, BigDecimal amount);
}
