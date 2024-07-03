package faang.school.accountservice.event;

import faang.school.accountservice.model.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class CancelPaymentEvent implements Event {
    private Long senderBalanceId;
    private Long receiverBalanceId;
    private Currency currency;
    private BigDecimal amount;
}