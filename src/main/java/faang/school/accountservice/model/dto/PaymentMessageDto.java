package faang.school.accountservice.model.dto;

import faang.school.accountservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMessageDto {

    private UUID idempotencyToken;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private Currency currency;
    private LocalDateTime scheduledAt;
}