package faang.school.accountservice.event;

import faang.school.accountservice.model.enums.Currency;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class NewPaymentEvent implements Event {
    private Long userId;
    private Long paymentId;
}