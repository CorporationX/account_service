package faang.school.accountservice.model.dto.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.accountservice.model.enums.OperationType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BalanceAuditDto(
    long balanceId,
    long version,
    BigDecimal actualBalance,
    BigDecimal authorizationBalance,
    OperationType type,
    LocalDateTime auditAt
) {
}
