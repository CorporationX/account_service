package faang.school.accountservice.dto.payment;

import faang.school.accountservice.model.payment_operation.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEventDto {
    private UUID paymentId;
    private String amount;
    private PaymentStatus status;
    private UUID accountFromId;
    private UUID accountToId;
}

