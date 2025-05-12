package faang.school.accountservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.event.Event;
import faang.school.accountservice.event.TransferSentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferSentEventPublisher extends AbstractEventPublisher {

    private static final String INPUT_DATA_RECIPIENT_ID = "recipientId";

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
        Object receiverIdValue = request.getInputData().get(INPUT_DATA_RECIPIENT_ID);
        if (receiverIdValue == null) {
            log.error("Recipient ID is missing in inputData for request {}", request.getIdempotencyKey());
            throw new IllegalArgumentException("Recipient ID is required but missing");
        }

        try {
            Long receiverId = Long.valueOf(String.valueOf(receiverIdValue));
            return new TransferSentEvent(request.getUserId(), receiverId);
        } catch (NumberFormatException e) {
            log.error("Failed to parse recipientId to Long for request {}: {}", request.getIdempotencyKey(), receiverIdValue);
            throw new IllegalArgumentException("Invalid recipientId format", e);
        }
    }
}
