package faang.school.accountservice.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.OpenAccountEventMapper;
import faang.school.accountservice.publisher.OpenAccountEventPublisher;
import faang.school.accountservice.repository.RequestTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationHandler implements RequestTaskHandler {
    private final OpenAccountEventPublisher publisher;
    private final OpenAccountEventMapper eventMapper;
    private final AccountMapper accountMapper;
    private final RequestTaskRepository requestTaskRepository;

    @Override
    public void execute(Request request, RequestTask task) {
        publisher.publish(eventMapper.toEntity(accountMapper.toDto(task.getAccount())));
        task.setRequestTaskStatus(RequestStatus.COMPLETED);
        requestTaskRepository.save(task);
    }

    @Override
    public Long getHandlerId() {
        return 5L;
    }
}
