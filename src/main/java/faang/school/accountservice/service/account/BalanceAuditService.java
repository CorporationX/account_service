package faang.school.accountservice.service.account;

import faang.school.accountservice.entity.account.Balance;
import faang.school.accountservice.entity.account.BalanceAudit;
import faang.school.accountservice.mapper.account.BalanceAuditMapper;
import faang.school.accountservice.repository.account.BalanceAuditRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceAuditService {

    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceAuditMapper balanceAuditMapper;

    @Transactional
    public void create(Balance balance) {
        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(balance);
        balanceAuditRepository.save(balanceAudit);
    }
}
