package faang.school.accountservice.listener.redis.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.listener.redis.abstracts.AbstractEventListener;
import faang.school.accountservice.service.pending.PendingOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("redis")
@Component
public class PendingOperationRedisListener extends AbstractEventListener<OperationMessage> {
    private final Topic topic;
    private final PendingOperationService pendingOperationService;

    public PendingOperationRedisListener(ObjectMapper javaTimeModuleObjectMapper, Topic pendingOperationTopic,
                                         PendingOperationService pendingOperationService) {
        super(javaTimeModuleObjectMapper, pendingOperationTopic);
        this.topic = pendingOperationTopic;
        this.pendingOperationService = pendingOperationService;
    }

    @Override
    public void saveEvent(OperationMessage event) {
        switch (event.getOperationType()) {
            case AUTHORIZATION -> pendingOperationService.authorization(event);
            case CLEARING -> pendingOperationService.clearing(event);
            case CANCELLATION, ERROR -> pendingOperationService.cancellation(event);
            default -> throw new RuntimeException("error");
        }
    }

    @Override
    public Class<OperationMessage> getEventType() {
        return OperationMessage.class;
    }

    @Override
    public void handleException(Exception exception) {
        log.error("Unexpected error, listen topic: {}", topic.getTopic(), exception);
        throw new RuntimeException(exception);
    }
}
