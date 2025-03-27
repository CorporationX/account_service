package faang.school.accountservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.event.Event;
import faang.school.accountservice.event.TransferSentEvent;
import faang.school.accountservice.model.Request;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class TransferSentEventPublisher extends AbstractEventPublisher {
    private final ChannelTopic transferTopic;

    public TransferSentEventPublisher(ObjectMapper objectMapper,
                                      StringRedisTemplate redisTemplate,
                                      ChannelTopic invitationTopic) {
        super(objectMapper, redisTemplate);
        this.transferTopic = invitationTopic;
    }

    @Override
    protected String getTopic() {
        return transferTopic.getTopic();
    }

    @Override
    protected Event getEvent(Request request) {
        Long receiverId = Long.valueOf(String.valueOf(request.getInputParams().get("receiverId")));
        return new TransferSentEvent(request.getUserId(), receiverId);
    }
}
