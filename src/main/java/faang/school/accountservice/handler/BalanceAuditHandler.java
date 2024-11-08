package faang.school.accountservice.handler;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.entity.RequestTask;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.RequestTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceAuditHandler implements RequestTaskHandler {
    private final BalanceAuditMapper auditMapper;
    private final BalanceAuditRepository balanceAuditRepository;
    private final RequestTaskRepository requestTaskRepository;

    @Override
    public void execute(Request request, RequestTask task) {
        balanceAuditRepository.save(auditMapper.toEntity(task.getAccount().getBalance()));
        task.setRequestTaskStatus(RequestStatus.COMPLETED);
        requestTaskRepository.save(task);
    }

    @Override
    public Long getHandlerId() {
        return 4L;
    }
}
