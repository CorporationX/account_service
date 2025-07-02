package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.mapper.request.RequestMapper;
import faang.school.accountservice.repository.request.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;

    @Transactional(rollbackFor = DataIntegrityViolationException.class)
    public RequestResponseDto createRequest(RequestCreateDto requestCreateDto) {
        Request request = requestMapper.toEntity(requestCreateDto);
        request.setIdempotencyToken(UUID.randomUUID());
        request.setLockedBy(requestCreateDto.getUserId());
        request.setStatus(RequestStatus.TO_DO);
        request.setActive(true);

        Request saved = requestRepository.save(request);
        return requestMapper.toDto(saved);
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    @Transactional
    public void completeRequest(Request request) {
        request.setStatus(RequestStatus.COMPLETED);
        request.setActive(false);
        requestRepository.save(request);
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    @Transactional
    public void updateStatus(Request request, RequestStatus status, String details) {
        request.setStatus(status);
        request.setDetails(details);
        requestRepository.save(request);
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    @Transactional
    public void updateStorage(Request request, Map<String, Object> storage) {
        request.setStorage(storage);
        requestRepository.save(request);
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    @Transactional
    public void close(Request request) {
        request.setActive(false);
        requestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public Optional<Request> getById(UUID id) {
        return requestRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<RequestResponseDto> getByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status)
                .stream()
                .map(requestMapper::toDto)
                .toList();
    }
}
