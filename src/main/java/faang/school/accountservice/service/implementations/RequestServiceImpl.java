package faang.school.accountservice.service.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.interfaces.RequestService;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public <T> T processRequest(
            UUID idempotencyToken,
            Long userId,
            RequestType type,
            String lockKey,
            Object input,
            Function<Request, T> handler
    ) {
        // 1. Поиск или создание запроса
        Request request = requestRepository.findById(idempotencyToken)
                .orElseGet(() -> createNewRequest(idempotencyToken, userId, type, lockKey, input));

        // 2. Проверка статуса
        if (request.getStatus() == RequestStatus.COMPLETED) {
            return deserializeResult(request.getStatusDetails());
        }

        // 3. Установка статуса IN_PROGRESS
        request.setStatus(RequestStatus.IN_PROGRESS);
        requestRepository.save(request);

        try {
            // 4. Выполнение бизнес-логики
            T result = handler.apply(request);

            // 5. Обновление статуса
            request.setStatus(RequestStatus.COMPLETED);
            request.setStatusDetails(serializeResult(result));
            request.setIsOpen(false);
            requestRepository.save(request);

            return result;
        } catch (Exception e) {
            request.setStatus(RequestStatus.CANCELED);
            request.setStatusDetails(e.getMessage());
            request.setIsOpen(false);
            requestRepository.save(request);
            throw e;
        }
    }

    private Request createNewRequest(UUID token, Long userId, RequestType type, String lockKey, Object input) {
        // Заполнение нового Request
        Request request = new Request();
        request.setIdempotencyToken(token);
        request.setUserId(userId);
        request.setType(type);
        request.setLockKey(lockKey);
        request.setInput(input);
        request.setStatus(RequestStatus.PENDING);
        request.setIsOpen(true);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        return requestRepository.save(request);
    }

    private <T> String serializeResult(T result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize result", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T deserializeResult(String json) {
        try {
            return (T) objectMapper.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize result", e);
        }
    }
}
