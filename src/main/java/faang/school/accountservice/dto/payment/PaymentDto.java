package faang.school.accountservice.dto.payment;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PaymentDto {

        private Long id;
        private UUID idempotencyKey;
        private String senderAccountNumber;
        private String receiverAccountNumber;
        private Currency currency;
        private BigDecimal amount;
        private PaymentStatus paymentStatus;
        private LocalDateTime scheduledAt;
        private LocalDateTime createdAt;
}