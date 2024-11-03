package faang.school.accountservice.util.fabrics;

import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.pending.Category;
import faang.school.accountservice.enums.pending.OperationStatus;
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

    public static OperationMessage buildOperationMessage(UUID id, UUID sourceAccountId) {
        return OperationMessage.builder()
                .operationId(id)
                .sourceAccountId(sourceAccountId)
                .build();
    }

    public static OperationMessage buildOperationMessage(OperationStatus status) {
        return OperationMessage.builder()
                .status(status)
                .build();
    }

    public static OperationMessage buildOperationMessage(UUID id, UUID sourceAccountId, UUID targetAccountId,
                                                         BigDecimal amount, Currency currency, Category category) {
        return OperationMessage.builder()
                .operationId(id)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .currency(currency)
                .category(category)
                .build();
    }

    public static OperationMessage buildOperationMessage(UUID id, UUID sourceAccountId, UUID targetAccountId,
                                                         BigDecimal amount, Currency currency, Category category,
                                                         OperationStatus status) {
        return OperationMessage.builder()
                .operationId(id)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .currency(currency)
                .category(category)
                .status(status)
                .build();
    }
}
