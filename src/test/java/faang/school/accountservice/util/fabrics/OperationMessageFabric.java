package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.pending.OperationType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class OperationMessageFabric {
    public static OperationMessage buildOperationMessage() {
        return OperationMessage.builder()
                .build();
    }

    public static OperationMessage buildOperationMessage(UUID id) {
        return OperationMessage.builder()
                .operationId(id)
                .build();
    }

    public static OperationMessage buildOperationMessage(OperationType type) {
        return OperationMessage.builder()
                .operationType(type)
                .build();
    }

    public static OperationMessage buildOperationMessage(UUID id, UUID accountId, BigDecimal amount, Currency currency) {
        return OperationMessage.builder()
                .operationId(id)
                .accountId(accountId)
                .amount(amount)
                .currency(currency)
                .build();
    }
}
