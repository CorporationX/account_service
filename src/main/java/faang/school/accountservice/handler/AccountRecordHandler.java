package faang.school.accountservice.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountRecordHandler implements RequestTaskHandler {
    private final AccountRepository accountRepository;
    private final RequestTaskRepository requestTaskRepository;

    @Override
    public void execute(Request request, RequestTask task) {
        accountRepository.save(task.getAccount());
        task.setRequestTaskStatus(RequestStatus.COMPLETED);
        requestTaskRepository.save(task);
    }

    @Override
    public Long getHandlerId() {
        return 2L;
    }
}

