package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.request.CreateRequestDto;
import faang.school.accountservice.dto.request.RequestEventDto;
import faang.school.accountservice.dto.request.ResponseRequestDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.exception.DuplicateKeyException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.exception.IllegalStatusTransitionException;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.publisher.RequestStatusPublisher;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.operation.AsyncRequestProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final AsyncRequestProcessor asyncRequestProcessor;
    private final RequestStatusPublisher requestStatusPublisher;

    @Override
    @Transactional
    public ResponseRequestDto createRequest(UUID idempotencyToken, CreateRequestDto dto) {
        validateOwner(dto);

        Optional<Request> existingOpt = requestRepository.findById(idempotencyToken);
        if (existingOpt.isPresent()) {
            return handleIdempotentRequest(existingOpt.get(), dto);
        }

        Request request = createAndSaveRequest(idempotencyToken, dto);

        asyncRequestProcessor.processAsync(request.getIdempotencyToken());

        return requestMapper.toResponseRequestDto(request);
    }


    @Override
    @Transactional
    public ResponseRequestDto updateRequestStatus(UUID idempotencyToken, RequestStatus status, String statusDetails) {

        Request request = requestRepository.findById(idempotencyToken)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Request with idempotencyToken %s not found", idempotencyToken)
                ));

        if (request.getRequestStatus().isFinal()) {
            throw new IllegalStatusTransitionException(
                    String.format("Cannot change status of final request: %s", request.getRequestStatus())
            );
        }

        request.changeStatus(status, statusDetails);
        request = requestRepository.save(request);

        publishEvent(request);

        return requestMapper.toResponseRequestDto(request);
    }

    private void validateOwner(CreateRequestDto dto) {
        boolean hasUser = dto.userId() != null;
        boolean hasProject = dto.projectId() != null;
        if (hasUser == hasProject) {
            throw new IllegalArgumentException("Must specify exactly one owner: userId or projectId");
        }
    }

    private ResponseRequestDto handleIdempotentRequest(Request existing, CreateRequestDto newRequest) {
        if (!Objects.equals(existing.getInputData(), newRequest.inputData())
                || !Objects.equals(existing.getUserId(), newRequest.userId())
                || !Objects.equals(existing.getProjectId(), newRequest.projectId())
                || existing.getOperationType() != newRequest.operationType()) {

            throw new DuplicateKeyException(
                    "Idempotency token already used with different request data"
            );
        }

        log.info("Idempotent request found by token {}, returning existing result.",
                existing.getIdempotencyToken());
        return requestMapper.toResponseRequestDto(existing);
    }

    private Request createAndSaveRequest(UUID idempotencyToken, CreateRequestDto dto) {
        try {
            Request request = requestMapper.toEntity(dto);
            request.setIdempotencyToken(idempotencyToken);
            request.changeStatus(RequestStatus.PENDING, null);

            return requestRepository.save(request);

        } catch (DataIntegrityViolationException e) {
            throw new DuplicateKeyException(
                    String.format("Open request with lock value already exists: %s", dto.lockValue()), e
            );
        }
    }

    private void publishEvent(Request request) {
        requestStatusPublisher.publish(new RequestEventDto(
                request.getIdempotencyToken(),
                request.getUserId(),
                request.getOperationType(),
                request.getRequestStatus(),
                LocalDateTime.now()
        ));
    }
}