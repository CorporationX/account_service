package faang.school.accountservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ClearPaymentEvent implements Event {
    private Long paymentId;
}