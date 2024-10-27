package faang.school.accountservice.dto.listener.pending;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.pending.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationMessageDto {
    private UUID operationId;
    private UUID accountId;
    private String idempotencyKey;
    private BigDecimal amount;
    private Currency currency;
    private OperationType operationType;
}
