package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.event.PaymentClearEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class PaymentClearEventListener extends AbstractEventListener implements MessageListener {
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("New message received: {}", message);
        PaymentClearEvent event;
        try {
            event = objectMapper.readValue(message.getBody(), PaymentClearEvent.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTopic() {
        return RedisTopics.PAYMENT_CLEAR.getTopic();
    }
}
