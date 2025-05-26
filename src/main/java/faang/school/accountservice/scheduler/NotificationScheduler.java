package faang.school.accountservice.scheduler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final RequestRepository requestRepository;
    private final NotificationService notificationService;

    @Value("${scheduler.updated-time}")
    private Long updatedTimeMillis;

    @Scheduled(fixedDelayString = "${scheduler.updated-time}")
    @Transactional
    public void sendNotification() {
        List<Request> recentlyUpdatedRequests = requestRepository
                .findRecentlyUpdated(Instant.ofEpochSecond(updatedTimeMillis));
        recentlyUpdatedRequests
                .forEach(request -> notificationService.sendStatusNotification(request.getIdempotencyToken()));
    }
}
