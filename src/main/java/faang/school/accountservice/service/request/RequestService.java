package faang.school.accountservice.service.request;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestGetDto;
import faang.school.accountservice.exception.DuplicateIdempotencyKeyException;
import faang.school.accountservice.exception.LockedRequestException;
import faang.school.accountservice.mapper.RequestMapper;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.publisher.EventPublisher;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.service.request.handler.RequestHandlerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static faang.school.accountservice.enums.RequestStatus.COMPLETED;
import static faang.school.accountservice.enums.RequestStatus.TODO;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;
    private final RequestHandlerFactory requestHandlerFactory;
    private final EventPublisher eventPublisher;

    @Transactional
    public RequestGetDto createRequest(RequestCreateDto requestCreateDto) {
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

    private RuntimeException getExceptionByMessage(DataIntegrityViolationException e) {
        if (e.getMessage().contains("idx_request_user_id")) {
            String errorMessage = "Запрос с таким ключом идемпотентности уже существует";
            log.error(errorMessage);
            return new DuplicateIdempotencyKeyException("Запрос в обработке");
        } else if (e.getMessage().contains("idx_request_lock_value_open")) {
            String errorMessage = "Запрос с таким значением блокировки уже открыт";
            log.error(errorMessage);
            return new LockedRequestException("Запрос в обработке");
        } else {
            log.error(e.getMessage(), e);
            return new RuntimeException("Невозможно создать запрос");
        }
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class)
    @Transactional
    public void updateRequest(Request request) {
        request.setRequestStatus(COMPLETED);
        request.setOpen(false);
        requestRepository.save(request);

        eventPublisher.publish(request);
    }

    @Scheduled(cron = "${account-service.process-request-cron}")
    public void processRequests() {
        log.info("тред {}", Thread.currentThread().getName());
        List<Request> requests = requestRepository.findByRequestStatus(TODO);
        requests.forEach(request -> {
            requestHandlerFactory.getHandler(request.getRequestType()).handle(request);
            updateRequest(request);
        });
    }
}
