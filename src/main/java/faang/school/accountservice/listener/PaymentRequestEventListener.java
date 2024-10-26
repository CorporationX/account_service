package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.listener.event.PaymentRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentRequestEventListener extends AbstractEventListener implements MessageListener {
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("New message received: {}", message);
        PaymentRequestEvent event;
        try {
            event = objectMapper.readValue(message.getBody(), PaymentRequestEvent.class);
        } catch (IOException e) {
            log.error("Error while parsing message");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTopic() {
        return RedisTopics.PAYMENT_REQUEST.getTopic();
    }
}
