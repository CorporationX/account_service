package faang.school.accountservice.service.audit;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.repository.balance.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BalanceAuditServiceImpl implements BalanceAuditService {
    private final BalanceAuditRepository balanceAuditRepository;

    @Transactional
    @Override
    public BalanceAudit saveAudit(Balance balance) {
        BalanceAudit balanceAudit = new BalanceAudit().builder()
                .authorizedBalance(balance.getAuthorizedBalance())
                .actualBalance(balance.getActualBalance())
                .createdAt(LocalDateTime.now())
                .number(balance.getAccount().getNumber())
                .build();
       return balanceAuditRepository.save(balanceAudit);
    }

    @Transactional
    @Override
    public void deleteAudit(Long auditId) {
        balanceAuditRepository.deleteById(auditId);
    }
}
