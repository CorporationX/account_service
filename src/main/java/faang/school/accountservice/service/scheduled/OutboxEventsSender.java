package faang.school.accountservice.service.scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.entity.OutboxEvent;
import faang.school.accountservice.repository.OutboxEventsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventsSender {
    @Value("${scheduled.sendPaymentsToKafka.batchSize}")
    private int batchSize;

    private final OutboxEventsRepository outboxEventsRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    @Scheduled(cron = "${scheduled.sendPaymentsToKafka.cron}")
    public void retryUnsentPayments() {
        log.info("Start sending outbox messages to Kafka");
        List<OutboxEvent> events = outboxEventsRepository.findTopUnprocessed(PageRequest.of(0, batchSize));

        if (events.isEmpty()) {
            return;
        }

        List<CompletableFuture<SendResult<String, Object>>> futures = new ArrayList<>();

        events.forEach(event -> {
            try {
                Object payload = objectMapper.readValue(event.getJsonPayload(), Class.forName(event.getClassType()));
                CompletableFuture<SendResult<String, Object>> sent = kafkaTemplate.send(
                        event.getKafkaTopic(), event.getId().toString(), payload);

                futures.add(sent);
            } catch (Exception e) {
                log.error("Failed to deserialize OutboxEvent: {}", event.getId(), e);
            }
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<UUID> successfullySent = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .map(sendResult -> {
                    String key = sendResult.getProducerRecord().key();
                    return key != null ? UUID.fromString(key) : null;
                })
                .filter(Objects::nonNull)
                .toList();

        if (!successfullySent.isEmpty()) {
            outboxEventsRepository.deleteOutboxEventsById(successfullySent);
        }
    }
}
