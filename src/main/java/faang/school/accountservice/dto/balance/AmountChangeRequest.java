package faang.school.accountservice.dto.balance;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.enums.ChangeBalanceType;
import faang.school.accountservice.enums.OperationType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AmountChangeRequest(
        @NotNull
        @JsonProperty(required = true)
        BigDecimal amount,

        @JsonProperty(required = true)
        OperationType operationType,

        @JsonProperty(required = true)
        ChangeBalanceType changeBalanceType
) {
}