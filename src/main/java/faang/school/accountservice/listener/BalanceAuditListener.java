package faang.school.accountservice.listener;

import faang.school.accountservice.event.BalanceChangeEvent;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BalanceAuditListener {
    private final BalanceAuditMapper balanceAuditMapper;
    private final BalanceAuditRepository balanceAuditRepository;

    @EventListener
    public void onBalanceChanged(BalanceChangeEvent event) {
        BalanceAudit audit = balanceAuditMapper.toAudit(event.balance(), event.operationId());
        balanceAuditRepository.save(audit);
    }

}
