package faang.school.accountservice.publisher;

import faang.school.accountservice.event.AuthorizationMessageResultEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

public class AuthorizationMessageResultEventPublisher extends AbstractEventPublisher<AuthorizationMessageResultEvent>{
    public AuthorizationMessageResultEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic paymentResultChannelTopic) {
        super(redisTemplate, paymentResultChannelTopic);
    }

    @Override
    public Class<AuthorizationMessageResultEvent> getInstance() {
        return AuthorizationMessageResultEvent.class;
    }
}
