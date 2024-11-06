package faang.school.accountservice.service;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.handler.RequestTaskHandler;
import faang.school.accountservice.repository.RequestRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import lombok.RequiredArgsConstructor;
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


    @Transactional
    public void executeRequest(UUID requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        List<RequestTask> requestTasks = requestTaskRepository.findByRequestId(requestId);

        for (RequestTask task : requestTasks) {
            // Фильтруем нужные хэндлеры по ID
            RequestTaskHandler handler = handlers.stream()
                    .filter(h -> h.getHandlerId().equals(task.getRequestTaskStatus()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Handler not found for task: " + task.getId()));

            // Выполняем шаг
            handler.execute(request, task); // здесь нужно отладить передачу конкретным хэндлерам

            // Обновляем состояние request_task
            task.setRequestTaskStatus(RequestStatus.COMPLETED.name());
            requestTaskRepository.save(task);

            // Обновляем состояние запроса
            request.setStatus(RequestStatus.IN_PROGRESS);
            requestRepository.save(request);
        }

        // После выполнения всех шагов обновляем статус запроса
        request.setStatus(RequestStatus.COMPLETED);
        requestRepository.save(request);
    }
}