package faang.school.accountservice.service.request;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.request.RequestStatus;
import faang.school.accountservice.enums.request.RequestTaskStatus;
import faang.school.accountservice.exception.RequestExecutorException;
import faang.school.accountservice.exception.RequestNotFoundException;
import faang.school.accountservice.messages.ErrorMessages;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import faang.school.accountservice.service.request.handler.RequestTaskHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static faang.school.accountservice.messages.ErrorMessages.HANDLER_NOT_FOUND;
import static faang.school.accountservice.messages.ErrorMessages.REQUEST_TASK_EXECUTION_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestExecutorServiceImpl implements RequestExecutorService {
    private final RequestRepository requestRepository;
    private final RequestTaskRepository requestTaskRepository;
    private final List<RequestTaskHandler> taskHandlers;
    private final RequestTaskExecutor executor;

    @Override
    @Transactional
    public void execute(UUID requestId) {
        Request request = getRequest(requestId);
        request.setStatus(RequestStatus.IN_PROGRESS);
        requestRepository.save(request);

        List<RequestTask> tasks = requestTaskRepository.findByRequestIdOrderByCreatedAt(requestId);

        for (RequestTask task : tasks) {
            if (!RequestTaskStatus.PENDING.equals(task.getStatus())) {
                continue;
            }
            try {
                RequestTaskHandler handler = getHandler(task.getHandler());
                executor.executeTaskWithNewTx(request, task, handler);
            } catch (Exception e) {
                log.error(REQUEST_TASK_EXECUTION_FAILED, e);
                throw new RequestExecutorException(REQUEST_TASK_EXECUTION_FAILED );
            }
        }
        request.setStatus(RequestStatus.COMPLETED);
        requestRepository.save(request);
    }

    private Request getRequest(UUID requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new RequestNotFoundException(ErrorMessages.REQUEST_NOT_FOUND));
    }

    private RequestTaskHandler getHandler(String handlerId) {
        return taskHandlers.stream()
                .filter(h -> h.getHandlerId().equals(handlerId))
                .findFirst()
                .orElseThrow(() -> new RequestExecutorException(String.format(HANDLER_NOT_FOUND, handlerId)));
    }
}
