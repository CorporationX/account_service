package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.request.RequestCreationDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.dto.request.RequestStatusResponseDto;
import faang.school.accountservice.dto.request.RequestUpdateDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.exception.OpenRequestExistsException;
import faang.school.accountservice.exception.RequestNotFoundException;
import faang.school.accountservice.mapper.request.RequestMapper;
import faang.school.accountservice.mapper.request.RequestUpdateMapper;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.request.handler.RequestHandler;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static faang.school.accountservice.messages.ErrorMessages.REQUEST_NOT_FOUND;
import static faang.school.accountservice.messages.ErrorMessages.USER_HAS_ALREADY_OPENED_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;
    private final List<RequestHandler> requestHandlers;
    private final RequestUpdateMapper updateMapper;

    @Value("${app.requests-thread-pool-size}")
    private int threadPoolSize;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    @Override
    @Transactional
    public RequestResponseDto createRequest(RequestCreationDto requestCreationDto) {
        Optional<Request> requestOptional = requestRepository.findById(requestCreationDto.getIdempotencyToken());
        if (requestOptional.isPresent()) {
            log.info("Request with idempotency token {} already exists",
                    requestCreationDto.getIdempotencyToken());
            return requestMapper.requestToRequestResponseDto(requestOptional.get());
        }
        Request requestToSave = requestMapper.requestCreationDtoToRequest(requestCreationDto);
        requestToSave.setLockId(requestToSave.getUserId());
        requestToSave.setOpen(true);
        requestToSave.setStatus(RequestStatus.PENDING);

        try {
            executorService.submit(() -> completeRequest(requestToSave));
            return requestMapper.requestToRequestResponseDto(requestRepository.save(requestToSave));
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create request after retries for userId {}",
                    requestCreationDto.getUserId(), e);
            throw new OpenRequestExistsException(USER_HAS_ALREADY_OPENED_REQUEST
                    .formatted(requestCreationDto.getUserId()));
        }
    }

    private void completeRequest(Request request) {
        requestHandlers.stream()
                .filter(handler -> handler.getRequestType().equals(request.getType()))
                .findFirst()
                .ifPresent(handler -> handler.handle(request));
    }

    @Override
    @Transactional
    public RequestResponseDto updateRequest(RequestUpdateDto requestUpdateDto) {
        Optional<Request> requestOptional = requestRepository.findById(requestUpdateDto.getIdempotencyToken());
        if (requestOptional.isEmpty()) {
            log.error(REQUEST_NOT_FOUND.formatted(requestUpdateDto.getIdempotencyToken()));
            throw new RequestNotFoundException(REQUEST_NOT_FOUND.formatted(requestUpdateDto.getIdempotencyToken()));
        }
        Request request = requestOptional.get();
        updateMapper.updateRequestFromDto(requestUpdateDto, requestOptional.get());
        return requestMapper.requestToRequestResponseDto(request);
    }

    @Override
    public RequestStatusResponseDto getStatus(UUID id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(id.toString()));
        return new RequestStatusResponseDto(
                request.getIdempotencyToken(),
                request.getStatus());
    }
}
