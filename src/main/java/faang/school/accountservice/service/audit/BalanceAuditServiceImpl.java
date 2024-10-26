package faang.school.accountservice.service.audit;

import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.BalanceAuditRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public void saveAudit(Balance balance) {
        BalanceAudit balanceAudit = new BalanceAudit().builder()
                .authorizedBalance(balance.getAuthorizedBalance())
                .actualBalance(balance.getActualBalance())
                .createdAt(LocalDateTime.now())
                .number(balance.getAccount().getNumber())
                .build();
        balanceAuditRepository.save(balanceAudit);
    }

    @Transactional
    public void updateAudit(Balance balance) {
        BalanceAudit audit = balanceAuditRepository.findById(balance.getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Audit with %s id not found", balance.getId())));
        audit.builder()
                .authorizedBalance(balance.getAuthorizedBalance())
                .actualBalance(balance.getActualBalance())
                .createdAt(LocalDateTime.now())
                .number(balance.getAccount().getNumber())
                .build();
        balanceAuditRepository.save(audit);
    }

    @Transactional
    public void deleteAudit(Long auditId) {
        balanceAuditRepository.deleteById(auditId);
    }
}
