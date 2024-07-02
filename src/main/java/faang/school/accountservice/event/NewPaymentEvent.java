package faang.school.accountservice.event;

import faang.school.accountservice.enums.Currency;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class NewPaymentEvent implements Event {
    private Long senderBalanceId;
    private Long receiverBalanceId;
    private Currency currency;
    private BigDecimal amount;
}