package faang.school.accountservice.dto.transaction;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.TransactionStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TransactionDto {

        private Long id;
        private UUID idempotencyKey;
        private String senderAccountNumber;
        private String receiverAccountNumber;
        private Currency currency;
        private BigDecimal amount;
        private TransactionStatus transactionStatus;
        private LocalDateTime scheduledAt;
        private LocalDateTime createdAt;
}