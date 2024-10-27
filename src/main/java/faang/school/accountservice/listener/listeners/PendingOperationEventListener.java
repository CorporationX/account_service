package faang.school.accountservice.listener.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.listener.pending.OperationMessageDto;
import faang.school.accountservice.listener.abstracts.AbstractEventListener;
import faang.school.accountservice.service.pending.PendingOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PendingOperationEventListener extends AbstractEventListener<OperationMessageDto> {
    private final Topic topic;
    private final PendingOperationService pendingOperationService;

    public PendingOperationEventListener(ObjectMapper javaTimeModuleObjectMapper, Topic pendingOperationTopic,
                                         PendingOperationService pendingOperationService) {
        super(javaTimeModuleObjectMapper, pendingOperationTopic);
        this.topic = pendingOperationTopic;
        this.pendingOperationService = pendingOperationService;
    }

    @Override
    public void saveEvent(OperationMessageDto event) {
        switch (event.getOperationType()) {
            case AUTHORIZATION -> pendingOperationService.authorization();
            case CANCELLATION -> pendingOperationService.cancellation();
            case CLEARING -> pendingOperationService.clearing();
            case ERROR -> pendingOperationService.error();
            default -> throw new RuntimeException("error");
        }
    }

    @Override
    public Class<OperationMessageDto> getEventType() {
        return OperationMessageDto.class;
    }

    @Override
    public void handleException(Exception exception) {
        log.error("Unexpected error, listen topic: {}", topic.getTopic(), exception);
        throw new RuntimeException(exception);
    }
}
