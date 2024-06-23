package faang.school.accountservice.service.balance_audit;

import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceAuditServiceImpl {

    private final BalanceAuditMapper balanceAuditMapper;
    private final BalanceAuditRepository balanceAuditRepository;

    @Transactional
    public void createNewAudit(Balance balance, long paymentId) {

        BalanceAudit audit = balanceAuditMapper.toAudit(balance, paymentId);

        balanceAuditRepository.save(audit);
    }
}
