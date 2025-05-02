package faang.school.accountservice.service.impl;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.exception.DuplicateIdempotencyKeyException;
import faang.school.accountservice.exception.LockedRequestException;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.publisher.EventPublisher;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.RequestService;
import faang.school.accountservice.service.handler.RequestHandler;
import faang.school.accountservice.service.handler.RequestHandlerFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Objects;
import java.util.UUID;

import static faang.school.accountservice.enums.RequestStatus.COMPLETED;
import static faang.school.accountservice.enums.RequestStatus.TODO;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;
    private final RequestHandlerFactory requestHandlerFactory;
    private final EventPublisher eventPublisher;
    private final UserContext userContext;

    @Transactional
    public RequestResponseDto createRequest(RequestCreateDto requestCreateDto) {
        if (requestCreateDto.getUserId() == null || !requestCreateDto.getUserId().equals(userContext.getUserId())) {
            throw new IllegalArgumentException("User ID mismatch with context");
        }

        Request request = requestMapper.toEntity(requestCreateDto);
        request.setLock(requestCreateDto.getUserId());
        request.setRequestStatus(TODO);
        request.setOpen(true);

        try {
            return requestMapper.toDto(requestRepository.save(request));
        } catch (DataIntegrityViolationException e) {
            throw getExceptionByMessage(e);
        }
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    @Transactional
    public void updateRequest(Request request) {
        request.setRequestStatus(COMPLETED);
        request.setOpen(false);
        log.info("Updating request {} for user {}", request.getIdempotencyKey(), request.getUserId());
        requestRepository.save(request);
        eventPublisher.publish(request);
    }

    @Override
    @Transactional(readOnly = true)
    public RequestResponseDto getRequest(UUID idempotencyKey) throws AccessDeniedException {
        Request request = requestRepository.findById(idempotencyKey)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Request with idempotency key %s not found", idempotencyKey)
                ));
        if (!request.getUserId().equals(userContext.getUserId())) {
            throw new AccessDeniedException("You can only access your own requests");
        }
        return requestMapper.toDto(request);
    }

    @Override
    @Transactional
    public void deleteRequest(UUID idempotencyKey) throws AccessDeniedException {
        Request request = requestRepository.findById(idempotencyKey)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Request with idempotency key %s not found", idempotencyKey)
                ));
        if (!request.getUserId().equals(userContext.getUserId())) {
            throw new AccessDeniedException("You don't have permission to delete this request");
        }
        if (request.isOpen() || request.getRequestStatus() == RequestStatus.PENDING) {
            throw new IllegalStateException("Cannot delete open or pending requests");
        }
        requestRepository.delete(request);
        log.info("Request {} deleted by user {}", idempotencyKey, userContext.getUserId());
    }

    @Scheduled(cron = "${account-service.process-request-cron}")
    public void processRequests() {
        log.info("Thread {}: starting to process requests", Thread.currentThread().getName());
        requestRepository.findByRequestStatus(TODO).stream()
                .peek(request -> log.info("Handling request {} for user {} of type {}",
                        request.getIdempotencyKey(), request.getUserId(), request.getRequestType()))
                .map(request -> {
                    RequestHandler handler = requestHandlerFactory.getHandler(request.getRequestType());
                    if (handler == null) {
                        log.error("No handler found for request type {}", request.getRequestType());
                        return null;
                    }
                    handler.handle(request);
                    return request;
                })
                .filter(Objects::nonNull)
                .forEach(this::updateRequest);
    }

    private RuntimeException getExceptionByMessage(DataIntegrityViolationException e) {
        if (e.getMessage().contains("idx_request_user_id")) {
            String errorMessage = "A request with the same idempotency key already exists";
            log.error(errorMessage);
            return new DuplicateIdempotencyKeyException("Request in processing...");
        } else if (e.getMessage().contains("idx_request_lock_value_open")) {
            String errorMessage = "A request with this lock value is already open";
            log.error(errorMessage);
            return new LockedRequestException("Request in processing...");
        } else {
            log.error(e.getMessage(), e);
            return new RuntimeException("Unable to create request");
        }
    }
}
