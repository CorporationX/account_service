package faang.school.accountservice.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.BalanceJpaRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceRecordHandler implements RequestTaskHandler {
    private final BalanceJpaRepository balanceRepository;
    private final RequestTaskRepository requestTaskRepository;

    @Override
    public void execute(Request request, RequestTask task) {
        balanceRepository.save(task.getAccount().getBalance());
        task.setRequestTaskStatus(RequestStatus.COMPLETED);
        requestTaskRepository.save(task);
    }

    @Override
    public Long getHandlerId() {
        return 3L;
    }
}
