package faang.school.accountservice.service;

import faang.school.accountservice.event.RequestEvent;
import faang.school.accountservice.kafka.producer.DataSender;
import faang.school.accountservice.kafka.producer.KafkaTopics;
import faang.school.accountservice.model.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventPublisherService {
    private final DataSender kafkaDataSender;

    @Async("eventSender")
    public void publishEvent(String topic, String eventType, Request req) {
        RequestEvent event = RequestEvent.builder()
                .idempotencyKey(req.getIdempotencyKey())
                .userId(req.getUserId())
                .requestType(req.getRequestType().name())
                .currentStatus(req.getRequestStatus().name())
                .statusDetails(req.getStatusDetails())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaDataSender.send(topic, event);
    }
}
