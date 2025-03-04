package faang.school.accountservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.annotations.NoTransactional;
import faang.school.accountservice.entity.OutboxEvent;
import faang.school.accountservice.exception.NonRetryableException;
import faang.school.accountservice.repository.OutboxEventsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OutboxEventService {
    private final OutboxEventsRepository outboxEventsRepository;
    private final ObjectMapper objectMapper;

    @NoTransactional
    public void save(UUID key, Class<?> classType, String kafkaTopic, Object payload) {
        try {
            String text = objectMapper.writeValueAsString(payload);
            OutboxEvent event = new OutboxEvent(key, classType.getName(), kafkaTopic, text);
            outboxEventsRepository.save(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payload for topic {}: {}", kafkaTopic, e.getMessage(), e);
            throw new NonRetryableException("Serialization error: " + e.getMessage());
        }
    }
}
