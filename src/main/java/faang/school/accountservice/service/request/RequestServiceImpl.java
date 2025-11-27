package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.request.CreateRequestDto;
import faang.school.accountservice.dto.request.RequestEventDto;
import faang.school.accountservice.dto.request.ResponseRequestDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.exception.DuplicateKeyException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.publisher.RequestStatusPublisher;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.operation.OperationHandler;
import faang.school.accountservice.service.operation.OperationHandlerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final OperationHandlerRegistry operationHandlerRegistry;
    private final RequestStatusPublisher requestStatusPublisher;

    @Override
    @Transactional
    public ResponseRequestDto createRequest(UUID idempotencyToken, CreateRequestDto dto) {
        boolean hasUser = dto.userId() != null;
        boolean hasProject = dto.projectId() != null;
        if (hasUser == hasProject) {
            throw new IllegalArgumentException("Must specify exactly one owner: userId or projectId");
        }

        Optional<Request> existingOpt = requestRepository.findById(idempotencyToken);
        if (existingOpt.isPresent()) {
            log.info("Idempotent request found by token {}, returning existing result.", idempotencyToken);
            return requestMapper.toResponseRequestDto(existingOpt.get());
        }

        if (requestRepository.existsByLockValueAndIsOpenTrue(dto.lockValue())) {
            throw new DuplicateKeyException(
                    String.format("Open request with lock value already exists: %s", dto.lockValue())
            );
        }

        Request request = requestMapper.toEntity(dto);
        request.setIdempotencyToken(idempotencyToken);
        request.setRequestStatus(RequestStatus.PENDING);
        request.setIsOpen(true);
        request = requestRepository.save(request);

        try {
            OperationHandler handler = operationHandlerRegistry.getHandler(request.getOperationType());
            handler.execute(request);

            request.setRequestStatus(RequestStatus.COMPLETED);
            request.setStatusDetails("Success");
            log.info("Business operation completed by handler: {}", handler.getClass().getSimpleName());
        } catch (Exception e) {
            request.setRequestStatus(RequestStatus.FAILED);
            request.setStatusDetails(e.getMessage());
            log.error("Business operation failed: {}", e.getMessage(), e);
        }

        request.setIsOpen(!request.getRequestStatus().isFinal());
        request = requestRepository.save(request);

        publishEvent(request);

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
            throw new IllegalStateException(
                    String.format("Cannot change status of final request: %s", request.getRequestStatus())
            );
        }

        request.setRequestStatus(status);
        request.setIsOpen(!status.isFinal());
        request.setStatusDetails(statusDetails);

        request = requestRepository.save(request);

        publishEvent(request);

        return requestMapper.toResponseRequestDto(request);
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