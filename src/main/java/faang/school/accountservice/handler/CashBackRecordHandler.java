package faang.school.accountservice.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.CashBackRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashBackRecordHandler implements RequestTaskHandler {
    private final CashBackRepository cashBackRepository;
    private final RequestTaskRepository requestTaskRepository;

    @Override
    public void execute(Request request, RequestTask task) {
        cashBackRepository.save(task.getAccount().getCashback());
        task.setRequestTaskStatus(RequestStatus.COMPLETED);
        requestTaskRepository.save(task);
    }

    @Override
    public Long getHandlerId() {
        return 6L;
    }
}

