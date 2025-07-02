package faang.school.accountservice.scheduler;

import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.publisher.request.RequestKafkaPublisher;
import faang.school.accountservice.repository.request.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestKafkaScheduler {

    private final RequestRepository requestRepository;
    private final RequestKafkaPublisher kafkaPublisher;

    private static final List<RequestStatus> FINAL_STATUSES = List.of(
        RequestStatus.COMPLETED,
        RequestStatus.CANCELLED
    );

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void processFinishedRequests() {
        List<UUID> requestIds = requestRepository.findAllIdsByStatusIn(FINAL_STATUSES);

        for (UUID requestId : requestIds) {
            try {
                Optional<Request> lockedRequest = requestRepository.findByIdForUpdate(requestId);
                if (lockedRequest.isEmpty()) {
                    continue;
                }

                Request request = lockedRequest.get();
                kafkaPublisher.sendMessage(request);
                log.info("Kafka уведомление отправлено по запросу: {}", requestId);
            } catch (Exception e) {
                log.error("Ошибка при отправке Kafka сообщения для запроса {}", requestId, e);
            }
        }
    }
}
