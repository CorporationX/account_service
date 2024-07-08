package faang.school.accountservice.dto.payment;

import faang.school.accountservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Long id;
    private BigInteger requesterNumber;
    private BigInteger receiverNumber;
    private Currency currency;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String message;
}