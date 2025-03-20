package faang.school.accountservice.service;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestReadDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.exception.BusinessException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.repository.RequestRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final AccountService accountService;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Retryable(
            retryFor = {DataIntegrityViolationException.class},
            backoff = @Backoff(delay = 100),
            maxAttempts = 2
    )
    @Transactional
    public RequestReadDto createRequest(RequestCreateDto createDto) {
        Optional<Request> existingRequest = requestRepository.findByIdempotentKey(
                createDto.getIdempotentKey()
        );
        if (existingRequest.isPresent()) {
            return requestMapper.toDto(existingRequest.get());
        }

        Request request = requestMapper.toEntity(createDto);
        Account author = accountService.findByAccountNumber(
                createDto.getAuthorAccountNumber()
        );
        Account receiver = accountService.findByAccountNumber(
                createDto.getReceiverAccountNumber()
        );
        request.setAuthor(author);
        request.setReceiver(receiver);

        entityManager.persist(request);
        return requestMapper.toDto(request);
    }

    @Transactional
    @Retryable(
            retryFor = {OptimisticLockException.class},
            backoff = @Backoff(delay = 100),
            maxAttempts = 2
    )
    public RequestReadDto startRequest(String idempotentKey) {
        try {
            Request request = getRequestByIdempotentKey(idempotentKey);
            validateIsOpenRequest(request);
            if (request.isLock()) {
                throw new BusinessException("Запрос уже выполняется");
            }
            request.setLock(true);
            request.setStatus(RequestStatus.IN_PROGRESS);

            Request savedRequest = requestRepository.save(request);
            requestRepository.flush();
            return requestMapper.toDto(savedRequest);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Сейчас выполняется другой запрос");
        }
    }

    @Transactional
    @Retryable(
            retryFor = {OptimisticLockException.class},
            backoff = @Backoff(delay = 100),
            maxAttempts = 2
    )
    public RequestReadDto closeRequest(String idempotentKey) {
        Request request = getRequestByIdempotentKey(idempotentKey);
        validateIsOpenRequest(request);
        if (!request.isLock()) {
            throw new BusinessException("Запрос не выполняется");
        }
        request.setLock(false);
        request.setOpen(false);
        request.setStatus(RequestStatus.COMPLETED);
        return requestMapper.toDto(requestRepository.save(request));
    }

    public RequestReadDto getRequest(String idempotentKey) {
        return requestMapper.toDto(getRequestByIdempotentKey(idempotentKey));
    }

    private void validateIsOpenRequest(Request request) {
        if (!request.isOpen()) {
            throw new BusinessException("Запрос уже закрыт");
        }
    }

    private Request getRequestByIdempotentKey(String key) {
        return requestRepository.findByIdempotentKey(key)
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));
    }
}
