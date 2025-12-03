package faang.school.accountservice.scheduler;

import faang.school.accountservice.dto.request.RequestEventDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.publisher.RequestStatusPublisher;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestScheduler {

    private final RequestRepository requestRepository;
    private final RequestStatusPublisher requestStatusPublisher;

    @Scheduled(cron = "${request.scheduler.cron}")
    public void publishOpenRequestEvents() {
        requestRepository.findAllByIsOpen(true).stream()
                .filter(this::shouldRepublish)
                .forEach(request -> {
                    requestStatusPublisher.publish(new RequestEventDto(
                            request.getIdempotencyToken(),
                            request.getUserId(),
                            request.getOperationType(),
                            request.getStatus(),
                            LocalDateTime.now()
                    ));
                    log.info("Published event for open request: {}", request.getIdempotencyToken());
                });
    }

    private boolean shouldRepublish(Request request) {
        return request.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5));
    }
}