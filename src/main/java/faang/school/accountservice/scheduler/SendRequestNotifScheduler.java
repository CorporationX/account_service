package faang.school.accountservice.scheduler;

import faang.school.accountservice.config.async.AsyncConfig;
import faang.school.accountservice.config.redis.RedisTopicProperties;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.message.producer.MessagePublisher;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendRequestNotifScheduler {

    private final RequestService requestService;
    private final MessagePublisher messagePublisher;
    private final RedisTopicProperties redisTopicProperties;
    private final RequestMapper requestMapper;

    @Value("${schedule.send-request-in-progress-notification.chunk-size}")
    private int limit;

    private final static int FIXED_RATE_MS = 200;

    @Scheduled(fixedRate = FIXED_RATE_MS)
    @Async(AsyncConfig.SCHEDULED_EXECUTOR)
    public void sendNotifications() {
        log.info("Sending notifications in thread {}", Thread.currentThread().getName());
        List<Request> requests = requestService.getRequestsToExecute(RequestType.REMITTANCE, limit);

        log.debug("Given requests to execute: " + requests.size());
        requests.parallelStream()
                .map(requestMapper::toRequestInProgressEvent)
                .forEach(event ->
                        messagePublisher.publish(redisTopicProperties.getRequestInProgressTopic(), event));
    }
}
