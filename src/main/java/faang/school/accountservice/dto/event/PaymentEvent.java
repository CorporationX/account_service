package faang.school.accountservice.dto.event;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.payment.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class PaymentEvent {
    private BigInteger requesterNumber;
    private BigInteger receiverNumber;
    private Currency currency;
    private BigDecimal amount;
    private PaymentStatus type;
}