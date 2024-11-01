package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestHandlerType;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.handler.request.RequestTaskHandler;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.RequestJpaRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestExecutorServiceImpl implements RequestExecutorService {
    private final RequestTaskRepository requestTaskRepository;
    private final RequestJpaRepository requestRepository;
    private final List<RequestTaskHandler<?>> handlers;
    private final AccountMapper accountMapper;

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
        AccountDto accountDto = accountMapper.toDto(request.getAccount());

        for (RequestTask task : tasks) {
            RequestTaskHandler<AccountDto> handler = (RequestTaskHandler<AccountDto>) handlerMap.get(task.getHandler());
            if (handler != null) {
                try {
                    handler.execute(accountDto);
                    task.setStatus(RequestStatus.COMPLETED);
                    requestTaskRepository.save(task);
                } catch (Exception e) {
                    task.setStatus(RequestStatus.FAILED);
                    requestTaskRepository.save(task);
                    break;
                }
            } else {
                throw new RuntimeException("Handler not found for task: " + task.getHandler());
            }
        }
        requestRepository.save(request);
    }
}
