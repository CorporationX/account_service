package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceAuditDto;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.balance.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BalanceAuditService {
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceAuditMapper balanceAuditMapper;

    public BalanceAudit createAudit(Balance balance, Long operationId) {
        BalanceAudit audit = balanceAuditMapper.toAuditEntity(balance);
        audit.setOperationId(operationId);
        return balanceAuditRepository.save(audit);
    }

    public List<BalanceAuditDto> getAuditHistory(Long accountId) {
        return balanceAuditMapper.toListAuditDto(
                balanceAuditRepository.findByAccountId(accountId)
        );
    }
}

