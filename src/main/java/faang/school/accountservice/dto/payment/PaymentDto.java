package faang.school.accountservice.dto.payment;

import faang.school.accountservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private UUID id;
    private String amount;
    private Currency currency;
    private String accountNumberFrom;
    private String accountNumberTo;
    private PaymentStatus status;
    private LocalDateTime clearScheduledAt;

    public enum PaymentStatus {
        PENDING,
        FORCED,
        CANCELED
    }
}
