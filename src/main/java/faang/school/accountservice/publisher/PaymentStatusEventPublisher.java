package faang.school.accountservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.model.event.PaymentStatusEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusEventPublisher extends AbstractEventPublisher<PaymentStatusEvent> {
    public PaymentStatusEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                       ObjectMapper objectMapper,
                                       @Qualifier("paymentStatusChannelTopic") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}