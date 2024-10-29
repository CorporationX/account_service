package faang.school.accountservice.service.balance.operation;

import faang.school.accountservice.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public interface Operation {

    BigDecimal calculateBalance(BigDecimal balance, BigDecimal amount);

    OperationType getChangeType();

    @Autowired
    default void register(OperationRegistry operationRegistry) {
        operationRegistry.registerOperation(this);
    }
}