package faang.school.accountservice.service;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.enums.RequestTaskStatus;
import faang.school.accountservice.handler.handlers.RequestTaskHandler;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestExecutorService {
    private final List<RequestTaskHandler> handlers;
    private final RequestRepository requestRepository;
    private final RequestTaskRepository requestTaskRepository;

    public void executeRequest(UUID requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found by id: " + requestId));

        List<RequestTask> tasks = requestTaskRepository.findAllByRequestId(requestId);
        for (RequestTask task : tasks) {
            if (!task.getStatus().equals(RequestTaskStatus.COMPLETED)) {
                RequestTaskHandler handler = handlers.stream()
                        .filter(h -> h.getHandlerId().equals(task.getHandler()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Handler not found for task: " + task.getId()));
                try {
                    handler.execute(request, task);
                    task.setStatus(RequestTaskStatus.COMPLETED);
                } catch (Exception e) {
                    task.setStatus(RequestTaskStatus.FAILED);
                } finally {
                    requestTaskRepository.save(task);
                }
            }
        }

        request.setStatus(RequestStatus.COMPLETED);
        requestRepository.save(request);
    }
}