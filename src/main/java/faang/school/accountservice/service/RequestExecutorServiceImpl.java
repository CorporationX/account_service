package faang.school.accountservice.service;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestHandlerType;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.handler.request.RequestTaskHandler;
import faang.school.accountservice.repository.RequestJpaRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static faang.school.accountservice.enums.RequestStatus.CREATE_ACCOUNT_RECORD;

@Service
@RequiredArgsConstructor
public class RequestExecutorServiceImpl implements RequestExecutorService {
    private final RequestTaskRepository requestTaskRepository;
    private final RequestJpaRepository requestRepository;
    private final List<RequestTaskHandler> handlers;

    @Transactional
    @Override
    public void executeRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        List<RequestTask> tasks = request.getRequestTasks();

        Map<RequestHandlerType, RequestTaskHandler<?>> handlerMap = new HashMap<>();
        for (RequestTaskHandler<?> handler : handlers) {
            handlerMap.put(handler.getHandlerId(), handler);
        }
        for (RequestTask task : tasks) {
            RequestTaskHandler<?> handler = handlerMap.get(task.getHandler());
            if (handler != null) {
                try {
                    // Получаем конкретную сущность для передачи в хэндлер
                    Object entity = getEntityForTask(task);
                    handler.execute(entity); // Выполняем шаг с конкретной сущностью

                    // Обновляем статус задачи в БД
                    task.setStatus(RequestStatus.COMPLETED);
                    requestTaskRepository.save(task);
                } catch (Exception e) {
                    task.setStatus(RequestStatus.FAILED);
                    requestTaskRepository.save(task);
                    break; // В случае ошибки, прерываем выполнение
                }
            }
        }

        request.setStatus(RequestStatus.COMPLETED);
        requestRepository.save(request);
    }

    // Метод для получения сущности на основе задачи
    private Object getEntityForTask(RequestTask task) {
        switch (task.getHandler()) {
            case CASHBACK_RECORD_HANDLER:
                return task.getAccount(); // Предположим, что для этого хэндлера нужна сущность Account
            // Добавьте другие случаи для других типов хэндлеров
            // Например:
            case CREATE_ACCOUNT_RECORD:
                return task.getAccount(); // Или другую соответствующую сущность
            // Добавьте другие случаи по мере необходимости
            default:
                throw new IllegalArgumentException("Unsupported handler type: " + task.getHandler());
        }
    }
}
