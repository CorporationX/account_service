package faang.school.accountservice.service;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestTaskStatus;
import faang.school.accountservice.enums.RollbackStatus;
import faang.school.accountservice.handler.handlers.RequestTaskHandler;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestExecutorService {
    private final List<RequestTaskHandler> handlers;
    private final RequestRepository requestRepository;
    private final RequestTaskRepository requestTaskRepository;

    @Async
    @Transactional
    public void executeRequest(UUID requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found by id: " + requestId));
        try {
            List<RequestTask> tasks = requestTaskRepository.findAllByRequestId(requestId);
            for (RequestTask task : tasks) {
                if (!task.getStatus().equals(RequestTaskStatus.COMPLETED)) {
                    RequestTaskHandler handler = handlers.stream()
                            .filter(h -> h.getHandlerId().equals(task.getHandler()))
                            .findFirst()
                            .orElseThrow(() -> new EntityNotFoundException("Handler not found for task: " + task.getId()));

                    handler.execute(request, task);
                    task.setStatus(RequestTaskStatus.COMPLETED);
                    requestTaskRepository.save(task);
                }
            }
        } catch (Exception e) {
            initiateRollback(request);
        }

        request.setStatus(RequestStatus.COMPLETED);
        requestRepository.save(request);
    }

    private void initiateRollback(Request request) {
        request.setRollback(RollbackStatus.IN_PROGRESS);
        requestRepository.save(request);

        List<RequestTask> tasks = requestTaskRepository.findAllByRequestId(request.getId());
        for (RequestTask task : tasks) {
            if (task.getStatus().equals(RequestTaskStatus.COMPLETED)) {
                RequestTaskHandler handler = handlers.stream()
                        .filter(h -> h.getHandlerId().equals(task.getHandler()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Handler not found for task: " + task.getId()));
                try {
                    handler.rollback(request, task);
                    task.setStatus(RequestTaskStatus.COMPLETED);
                }catch (Exception e) {
                    task.setRollback(RollbackStatus.FAILED);
                    task.setRollbackReason(e.getMessage());
                } finally {
                    requestTaskRepository.save(task);
                }
            }
        }

        request.setRollback(RollbackStatus.COMPLETED);
        requestRepository.save(request);
    }
}