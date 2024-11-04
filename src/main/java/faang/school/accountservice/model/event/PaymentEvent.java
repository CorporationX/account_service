package faang.school.accountservice.model.event;

import faang.school.accountservice.model.enums.OperationType;
import faang.school.accountservice.model.enums.RequestType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    @NotNull
    private UUID idempotencyToken;
    @NotNull
    private Long accountId;
    @NotNull
    private RequestType requestType;
    @NotNull
    private OperationType operationType;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;
    @NotNull
    private LocalDateTime sentDateTime;
}