package faang.school.accountservice.service;

import faang.school.accountservice.background_worker.RequestEventsOutboxProcessor;
import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.RequestEventDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestVersion;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.exception.ServiceUnavailableException;
import faang.school.accountservice.mapper.RequestEventMapper;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.repository.RequestRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestEventService requestEventService;
    private final RequestRepository requestRepository;
    private final UserServiceClient userServiceClient;
    private final RequestMapper requestMapper;
    private final RequestEventMapper requestEventMapper;
    private final RequestEventsOutboxProcessor requestEventsOutboxProcessor;

    @Transactional
    public void createRequest(CreateRequestDto createRequestDto) {
        if (!doesUserExist(createRequestDto.userId())) {
            throw new ResourceNotFoundException("User with id: %d is not found".formatted(createRequestDto.userId()));
        }

        var request = requestMapper.toEntity(createRequestDto);
        request.setOpened(true);
        request.setRequestStatus(RequestStatus.TODO);
        request.setRequestVersion(RequestVersion.V1);

        requestRepository.save(request);
        createAndPublisRequestEvent(requestEventMapper.toRequestEventDto(createRequestDto));
    }

    @Transactional
    public void updateRequestStatusByToken(UUID requestToken, RequestStatus newRequestStatus) {
        var request = getRequestByToken(requestToken);

        if (request.getRequestStatus() == newRequestStatus) {
            return;
        }

        checkIsRequestOpened(requestToken, request);

        if (newRequestStatus == RequestStatus.DONE || newRequestStatus == RequestStatus.CANCELLED) {
            request.setOpened(false);
        }
        request.setRequestStatus(newRequestStatus);

        requestRepository.save(request);
        createAndPublisRequestEvent(requestMapper.toRequestEventDto(request));
    }

    @Transactional
    public void updateRequestBodyByToken(UUID requestToken, Map<String, Object> newBody) {
        var request = getRequestByToken(requestToken);

        checkIsRequestOpened(requestToken, request);

        request.setBody(newBody);

        requestRepository.save(request);
        createAndPublisRequestEvent(requestMapper.toRequestEventDto(request));
    }

    private void createAndPublisRequestEvent(RequestEventDto requestEventDto) {
        requestEventService.create(requestEventDto);
        requestEventsOutboxProcessor.newRequestEventsAdded();
    }

    private Request getRequestByToken(UUID requestToken) {
        return requestRepository.findById(requestToken).orElseThrow(() ->
                new ResourceNotFoundException("Request with id: %s is not found".formatted(requestToken)));
    }

    private static void checkIsRequestOpened(UUID requestToken, Request request) {
        if (!request.isOpened()) {
            throw new DataValidationException("Request %s is already closed".formatted(requestToken));
        }

        switch (request.getRequestStatus()) {
            case DONE -> throw new DataValidationException("Request %s is already done".formatted(requestToken));
            case CANCELLED -> throw new DataValidationException("Request %s is already cancelled"
                    .formatted(requestToken));
        }
    }

    private boolean doesUserExist(long userId) {
        try {
            userServiceClient.getUser(userId);
            return true;
        } catch (FeignException.NotFound ex) {
            return false;
        } catch (FeignException ex) {
            throw new ServiceUnavailableException("Unable to verify user existence: %s".formatted(ex.getMessage()),
                    ex);
        }
    }
}
