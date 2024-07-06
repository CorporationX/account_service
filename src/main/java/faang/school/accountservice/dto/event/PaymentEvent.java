package faang.school.accountservice.dto.event;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEvent {
    private Long paymentId;
    private BigInteger requesterNumber;
    private BigInteger receiverNumber;
    private Currency currency;
    private BigDecimal amount;
    private PaymentStatus type;
}