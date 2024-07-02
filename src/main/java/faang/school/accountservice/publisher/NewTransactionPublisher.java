package faang.school.accountservice.publisher;

import faang.school.accountservice.event.NewTransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewTransactionPublisher implements MessagePublisher<NewTransactionEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic inviteSentTopic;

    @Override
    public void publish(NewTransactionEvent event) {
        redisTemplate.convertAndSend(inviteSentTopic.getTopic(), event);
        log.info("Published new transaction event: {}", event);
    }
}