package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.event.PaymentEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class PaymentKeyGenerator {
    private final JsonMapper<PaymentEvent> eventJsonMapper;

    public UUID generateKey(PaymentEvent paymentEvent) {
        String json = eventJsonMapper.convertToJson(paymentEvent);
        return UUID.fromString(json);
    }
}