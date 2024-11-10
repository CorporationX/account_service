package faang.school.accountservice.dto.payment.response;

import faang.school.accountservice.enums.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorPaymentResponse {
    private UUID operationId;
    private PaymentStatus paymentStatus;
}
