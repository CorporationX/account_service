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
    @NotNull(groups = {AuthorizePaymentEventMarker.class, CancelPaymentEventMarker.class})
    private UUID idempotencyToken;
    @NotNull(groups = {AuthorizePaymentEventMarker.class, CancelPaymentEventMarker.class})
    private Long accountId;
    @NotNull(groups = {AuthorizePaymentEventMarker.class})
    private RequestType requestType;
    @NotNull(groups = {AuthorizePaymentEventMarker.class, CancelPaymentEventMarker.class})
    private OperationType operationType;
    @NotNull(groups = {AuthorizePaymentEventMarker.class})
    @DecimalMin(groups = {AuthorizePaymentEventMarker.class},
            value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;
    @NotNull(groups = {AuthorizePaymentEventMarker.class, CancelPaymentEventMarker.class})
    private LocalDateTime sentDateTime;

    public interface AuthorizePaymentEventMarker {};
    public interface CancelPaymentEventMarker {};
    public interface ClearingPaymentEventMarker {};

}