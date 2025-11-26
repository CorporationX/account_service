package faang.school.accountservice.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSchedule {
    private final NotificationPendingService notificationSchedulerService;

    @Scheduled(fixedDelayString = "${scheduler.notification.delay}")
    public void triggerNotificationProcessing(){
        notificationSchedulerService.processPendingNotifications();
    }
}
