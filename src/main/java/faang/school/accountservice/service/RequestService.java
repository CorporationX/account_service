package faang.school.accountservice.service;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.ResponseRequestDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.repository.RequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public Request getRequestByIdempotentToken(String token) {
        return requestRepository.findByIdempotentTokenForUpdate(token).orElse(null);
    }

    public Request getRequestBuValueLock(String value) {
        return requestRepository.findByValueLock(value)
                .orElseThrow(() -> new IllegalArgumentException("this value = " + value + "is null"));
    }

    @Transactional
    public ResponseRequestDto createRequest(CreateRequestDto createRequestDto) {
        validateRequest(createRequestDto);

        Request request = requestMapper.toEntity(createRequestDto);

        request.setStatus(RequestStatus.IN_PROGRESS);
        request.setIsOpen(false);

        requestRepository.save(request);

        log.info("______________________________________________________________________request is open: {}", createRequestDto.idempotentToken());

        return requestMapper.toDto(request);
    }

    @Transactional
    @Retryable(retryFor = SQLException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void openRequest(Request request) {
        request.setIsOpen(true);
        request.setValueLock(request.getUserId().toString());
        request.setStatus(RequestStatus.PENDING);

        requestRepository.save(request);
    }

    @Transactional
    @Retryable(retryFor = SQLException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void closeRequest(Request request) {
        request.setIsOpen(false);
        request.setValueLock(null);
        request.setStatus(RequestStatus.SUCCESS);

        requestRepository.save(request);
    }

    private void validateRequest(CreateRequestDto createRequestDto) {
        if (getRequestByIdempotentToken(createRequestDto.idempotentToken()) != null) {
            throw new IllegalArgumentException("Token = " + createRequestDto.idempotentToken() + "is occupied");
        }
        try {
            userServiceClient.getUser(createRequestDto.userId());
        } catch (NullPointerException e) {
            userContext.setUserId(createRequestDto.userId());
            userServiceClient.getUser(createRequestDto.userId());
        }
    }
}
