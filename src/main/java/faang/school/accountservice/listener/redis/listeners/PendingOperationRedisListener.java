package faang.school.accountservice.listener.redis.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.exception.ApiException;
import faang.school.accountservice.exception.pending.UnknownOperationException;
import faang.school.accountservice.listener.redis.abstracts.AbstractEventListener;
import faang.school.accountservice.service.pending.PendingOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.listener.Topic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "redis")
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
    public void saveEvent(OperationMessage operation) {
        switch (operation.getStatus()) {
            case PENDING -> pendingOperationService.authorization(operation);
            case CLEARING -> pendingOperationService.clearing(operation);
            case CANCELLATION, ERROR -> pendingOperationService.cancellation(operation);
            default -> throw new UnknownOperationException(operation.getStatus());
        }
    }

    @Override
    public Class<OperationMessage> getEventType() {
        return OperationMessage.class;
    }

    @Override
    public void handleException(Exception exception) {
        log.error("Unexpected error, listen topic: {}", topic.getTopic(), exception);
        throw new ApiException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
