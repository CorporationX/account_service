package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.event.PaymentEvent;
import faang.school.accountservice.handler.event.EventHandler;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class PaymentEventListener extends AbstractListener<PaymentEvent> {
    public PaymentEventListener(ObjectMapper objectMapper, List<EventHandler<PaymentEvent>> eventHandlers) {
        super(objectMapper, eventHandlers);
    }

    @Override
    protected PaymentEvent listenEvent(Message message) throws IOException {
        return objectMapper.readValue(message.getBody(), PaymentEvent.class);
    }
}