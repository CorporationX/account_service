package faang.school.accountservice.service.impl;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.Request.RequestDto;
import faang.school.accountservice.dto.Request.RequestStatusDto;
import faang.school.accountservice.entity.IdempotencyToken;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.exception.RequestProcessingException;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.repository.TokenRepository;
import faang.school.accountservice.service.RequestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@RequiredArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {
    private static final String REQUEST_IN_PROCESSING = "You`r request in processing...";
    private static final int THREAD_POOL_SIZE = 10;

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserContext userContext;
    private final TokenRepository tokenRepository;
    private final Map<Long, BlockingQueue<RequestDto>> userQueues = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    @Override
    @Transactional
    public String createRequest(RequestDto requestDto, IdempotencyToken idempotencyToken) {

        Long userId = userContext.getUserId();
        BlockingQueue<RequestDto> userQueue = userQueues.computeIfAbsent(userId, k -> new LinkedBlockingQueue<>());
        try {
            userQueue.put(requestDto);
            //TODO Оповещение о добавлении в очередь
            log.info("Request for userId = {} added to the queue", userId);

            CompletableFuture<RequestDto> futureRequest = CompletableFuture
                    .supplyAsync(() -> requestBalancer(userId, idempotencyToken), executorService);

            futureRequest.thenAccept(result -> {
                log.info("Request with input data completed {}", result.getInputData());
                //TODO оповвещение о готовности
            });

            return REQUEST_IN_PROCESSING;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while adding request to the queue", e);
            throw new RequestProcessingException("Error processing request for request with token= " + idempotencyToken);
        }
    }

    @Transactional
    @Override
    public RequestDto updateRequestStatus(Long requestId, RequestStatusDto requestStatusDto) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request with id = " + requestId + " not found"));
        request.setStatus(requestStatusDto.getStatus());
        request.setStatusDetails(requestStatusDto.getStatusDetails());
        Request savedRequest = requestRepository.save(request);
        log.info("Request with id = {} status was update", savedRequest.getId());
        //TODO оповещение о смене статуса
        return requestMapper.toDto(savedRequest);
    }

    @Override
    public RequestDto updateIsOpenFlag(Long requestId, boolean isOpen) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request with id = " + requestId + " not found"));
        request.setOpen(isOpen);
        Request savedRequest = requestRepository.save(request);
        log.info("Request with id = {} open/close flag was update", savedRequest.getId());
        //TODO оповещение о открытии\ закрытии запроса
        return requestMapper.toDto(savedRequest);
    }

    @Override
    public RequestDto updateInputData(Long requestId, Map<String, Object> inputData) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request with id = " + requestId + " not found"));
        request.setInputData(inputData);
        Request savedRequest = requestRepository.save(request);
        log.info("Request with id = {} input data was update", savedRequest.getId());
        return requestMapper.toDto(savedRequest);
    }

    private RequestDto requestBalancer(Long userId, IdempotencyToken idempotencyToken) {

        try {
            RequestDto requestDto = userQueues.get(userId).take();
            log.info("Processing request for userId = {}", userId);
            //TODO Оповещение о смене статуса запроса
            Request request = requestMapper.toEntity(requestDto);
            request.setStatus(RequestStatus.IN_PROGRESS);
            Optional<IdempotencyToken> tokenOpt = tokenRepository.findByToken(idempotencyToken.getToken());

            if (tokenOpt.isPresent()) {
                Request existingRequest = tokenOpt.get().getRequest();

                if (requestRepository.existsByInputDataAndUserId(existingRequest.getInputData(), userId)) {
                    if (isInputDataEqual(existingRequest, request)) {

                        log.info("Request already exists for userId = {}", userId);
                        existingRequest.setStatus(RequestStatus.REPEATING);
                        //TODO Оповещение о смене статуса запроса
                        return requestMapper.toDto(existingRequest);
                    }
                }
            }

            request.setUserId(userId);
            request.setLockValue(userId);
            request.setOpen(true);
            request.setIdempotencyToken(idempotencyToken);
            Request savedRequest = requestRepository.save(request);
            savedRequest.setStatus(RequestStatus.COMPLETED);
            log.info("Request for userId = {} processed successfully", userId);
            return requestMapper.toDto(savedRequest);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error processing request for userId = {}", userId, e);

            throw new RequestProcessingException("Error processing request for user with id = " + userId);
        }
    }

    private boolean isInputDataEqual(Request existRequest, Request request) {
        if (existRequest.getInputData() == null && request.getInputData() == null) {
            return true;
        }
        if (existRequest.getInputData() == null || request.getInputData() == null) {
            return false;
        }
        return Objects.equals(existRequest.getInputData(), request.getInputData());
    }


}
