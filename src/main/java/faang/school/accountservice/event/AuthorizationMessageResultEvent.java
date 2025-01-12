package faang.school.accountservice.event;

import faang.school.accountservice.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthorizationMessageResultEvent {
    private String accountNumber;
    private PaymentStatus paymentStatus;
    private String idempotencyToken;

    public AuthorizationMessageResultEvent(String accountNumber, String idempotencyToken) {
        this.accountNumber = accountNumber;
        this.idempotencyToken = idempotencyToken;
    }
}
