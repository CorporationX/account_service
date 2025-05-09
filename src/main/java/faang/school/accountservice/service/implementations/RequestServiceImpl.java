package faang.school.accountservice.service.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestType;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.interfaces.RequestService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public <T> T processRequest(
            String idempotencyToken,
            Long userId,
            RequestType type,
            String lockKey,
            Map<String, Object> input,
            Function<Request, T> handler
    ) {
        Request request = requestRepository.findById(idempotencyToken)
                .orElseGet(() -> createNewRequest(idempotencyToken, userId, type, lockKey, input));

        if (request.getStatus() == RequestStatus.COMPLETED) {
            return deserializeResult(request.getStatusDetails());
        }

        request.setStatus(RequestStatus.IN_PROGRESS);
        requestRepository.save(request);

        try {
            T result = handler.apply(request);

            request.setStatus(RequestStatus.COMPLETED);
            request.setStatusDetails(serializeResult(result));
            request.setOpen(false);
            requestRepository.save(request);

            return result;
        } catch (Exception e) {
            request.setStatus(RequestStatus.CANCELED);
            request.setStatusDetails(e.getMessage());
            request.setOpen(false);
            requestRepository.save(request);
            throw e;
        }
    }

    private Request createNewRequest(String token, Long userId, RequestType type, String lockKey, Map<String, Object> input) {
        Request request = new Request();
        request.setIdempotencyToken(token);
        request.setUserId(userId);
        request.setType(type);
        request.setLockKey(lockKey);
        request.setInput(input);
        request.setStatus(RequestStatus.PENDING);
        request.setOpen(true);
        request.setCreatedAt(Instant.now());
        request.setUpdatedAt(Instant.now());
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
