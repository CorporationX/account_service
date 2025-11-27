package faang.school.accountservice.service.operation;

import faang.school.accountservice.enums.request.OperationType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OperationHandlerRegistry {

    private final Map<OperationType, OperationHandler> registry = new HashMap<>();

    public OperationHandlerRegistry(List<OperationHandler> handlers) {
        for (OperationHandler handler : handlers) {
            registry.put(handler.getSupportedOperationType(), handler);
        }
    }

    public OperationHandler getHandler(OperationType type) {
        return registry.get(type);
    }
}