package faang.school.accountservice.service.impl.audit;

import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.audit.BalanceAuditMapper;
import faang.school.accountservice.model.dto.audit.BalanceAuditDto;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.entity.BalanceAudit;
import faang.school.accountservice.model.enums.OperationType;
import faang.school.accountservice.repository.AuditRepository;
import faang.school.accountservice.service.BalanceAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceAuditServiceImpl implements BalanceAuditService {
    private final AuditRepository auditRepository;
    private final BalanceAuditMapper balanceAuditMapper;

    @Override
    public void saveBalanceAudit(Balance balance, OperationType type) {
        BalanceAudit balanceAudit = BalanceAudit.builder()
                .balance(balance)
                .type(type)
                .build();
        auditRepository.save(balanceAudit);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceAuditDto getBalanceAudit(long balanceAuditId) {
        BalanceAudit balanceAudit = auditRepository.findById(balanceAuditId)
                .orElseThrow(() -> new DataValidationException("Not found balanceAudit: " + balanceAuditId));
        return balanceAuditMapper.toDto(balanceAudit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BalanceAuditDto> getAllBalanceAudit() {
        List<BalanceAudit> balanceAudits = auditRepository.findAll();
        return balanceAuditMapper.toDto(balanceAudits);
    }
}
