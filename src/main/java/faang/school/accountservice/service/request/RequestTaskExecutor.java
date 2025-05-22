package faang.school.accountservice.service.request;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request.RequestTaskStatus;
import faang.school.accountservice.exception.RequestTaskExecutionException;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import faang.school.accountservice.service.request.handler.RequestTaskHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static faang.school.accountservice.messages.ErrorMessages.ERROR_REQUEST_TASK;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestTaskExecutor {
    private final RequestRepository requestRepository;
    private final RequestTaskRepository requestTaskRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeTaskWithNewTx(Request request, RequestTask task, RequestTaskHandler handler) {
        try {
            executeTask(request, task, handler);
        } catch (RuntimeException  e) {
            log.error(String.format(ERROR_REQUEST_TASK, task.getId()), e);
            task.setStatus(RequestTaskStatus.FAILED);
            request.setStatus(RequestStatus.FAILED);
            task.setUpdatedAt(LocalDateTime.now());
            requestTaskRepository.save(task);
            requestRepository.save(request);
            throw new RequestTaskExecutionException(String.format(ERROR_REQUEST_TASK, task.getId()));
        }
    }

    private void executeTask(Request request, RequestTask task, RequestTaskHandler handler) {
        handler.execute(request, task);
        task.setStatus(RequestTaskStatus.DONE);
        task.setUpdatedAt(LocalDateTime.now());
        requestTaskRepository.save(task);
    }
}
