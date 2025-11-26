package faang.school.accountservice.service.notification;

import faang.school.accountservice.dto.NotificationMessageDto;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.notification.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPendingService {

    private final RequestRepository requestRepository;
    private final MessagePublisher messagePublisher;
    private final NotificationMessageFactory messageFactory;

    @Async
    public void processPendingNotifications() {
        requestRepository.findByNotificationPendingTrue()
                .stream()
                .forEach(request -> {
                    String message = switch (request.getPendingNotificationType()) {
                        case CREATED -> messageFactory.buildCreatedMessage(request);
                        case STATUS_UPDATED -> messageFactory.buildStatusUpdatedMessage(request);
                        case FLAG_UPDATED -> messageFactory.buildFlagUpdatedMessage(request);
                        case CONTEXT_UPDATED -> messageFactory.buildContextUpdatedMessage(request);
                    };

                    messagePublisher.publish(
                            new NotificationMessageDto(
                                    request.getUserId(),
                                    request.getIdempotencyKey(),
                                    message
                            )
                    );

                    request.setNotificationPending(false);
                    requestRepository.save(request);
                });
    }
}
