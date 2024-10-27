package faang.school.accountservice.service.balance.operation;

import faang.school.accountservice.enums.OperationType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OperationStorage {

    private final Map<OperationType, Operation> balanceChanges = new HashMap<>();

    public void registerOperation(Operation operation) {
        balanceChanges.put(operation.getChangeType(), operation);
    }

    public Operation getOperation(OperationType operationType) {
        Operation operation = balanceChanges.get(operationType);
        if (operation == null) {
            throw new IllegalArgumentException("Not found operation : " + operationType);
        }
        return operation;
    }
}
