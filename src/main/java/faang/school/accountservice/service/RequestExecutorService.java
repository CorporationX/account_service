package faang.school.accountservice.service;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.handler.RequestTaskHandler;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

        List<RequestTask> tasks = requestTaskRepository.findAllByRequestId(requestId);
        for (RequestTask task : tasks) {
            RequestTaskHandler handler = handlers.stream()
                    .filter(h -> h.getHandlerId().equals(task.getCurrentHandlerStep()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Handler not found for task: " + task.getId()));
            handler.execute(request, task);
        }
        request.setStatus(RequestStatus.COMPLETED);
        requestRepository.save(request);

    }


}
