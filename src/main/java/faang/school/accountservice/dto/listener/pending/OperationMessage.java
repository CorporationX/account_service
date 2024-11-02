package faang.school.accountservice.dto.listener.pending;

import faang.school.accountservice.enums.pending.Category;
import faang.school.accountservice.enums.pending.OperationStatus;
import faang.school.accountservice.enums.Currency;
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
public class OperationMessage {
    private UUID operationId;
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private String idempotencyKey;
    private BigDecimal amount;
    private Currency currency;
    private Category category;
    private OperationStatus status;
}
