package faang.school.accountservice.event.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAuthorizationEventDto {
    private UUID operationToken;
    private UUID accountFromId;
    private UUID accountToId;
    private UUID currencyId;
    private BigDecimal amount;
    //TODO: видимо тот, что from
    private Long userId;
}
