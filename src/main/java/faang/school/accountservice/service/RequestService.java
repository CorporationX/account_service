package faang.school.accountservice.service;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.event.RequestEvent;
import faang.school.accountservice.exception.ConcurrentRequestException;
import faang.school.accountservice.exception.ConflictException;
import faang.school.accountservice.kafka.producer.DataSender;
import faang.school.accountservice.kafka.producer.KafkaTopics;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.repository.RequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository repository;
    private final RequestMapper mapper;
    private final DataSender kafkaDataSender;
    private final KafkaTopics kafkaTopics;

    @Transactional
    public RequestDto createRequest(CreateRequestDto createRequestDto) {

        Optional<Request> existingOpt = repository.findById(createRequestDto.getIdempotencyKey());
        if (existingOpt.isPresent()) {
            Request existing = existingOpt.get();
            if (!Objects.equals(existing.getInputData(), createRequestDto.getInputData())) {
                throw new ConflictException("Idempotency key conflict with different input data.");
            }
            return mapper.toDto(existing);
        }

        repository.findByLockKeyAndIsOpenTrue(createRequestDto.getLockKey())
                .ifPresent(lockedRequest -> {
                    throw new ConcurrentRequestException("An active request with this lock key already exists.");
                });

        Request savedRequest = mapper.toEntity(createRequestDto);
        RequestDto dto = mapper.toDto(repository.save(savedRequest));

        publishEvent(kafkaTopics.getRequestEventsTopic(), "request-created", savedRequest);
        return dto;
    }

    @Transactional
    public void updateStatus(UUID idempotencyKey, RequestStatus requestStatus, String statusDetails) {
        Request request = validate(idempotencyKey);

        request.setRequestStatus(requestStatus);
        request.setStatusDetails(statusDetails);
        Request updated = repository.save(request);

        publishEvent(kafkaTopics.getRequestEventsTopic(), "request-status-changed", updated);
    }

    @Transactional
    public void closeRequest(UUID idempotencyKey) {
        Request request = validate(idempotencyKey);

        request.setOpen(false);
        Request closed = repository.save(request);

        publishEvent(kafkaTopics.getRequestEventsTopic(), "request-closed", closed);
    }

    private Request validate(UUID idempotencyKey) {
        return repository.findById(idempotencyKey)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
    }

    @Async("taskExecutor")
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
