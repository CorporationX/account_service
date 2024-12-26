package faang.school.accountservice.service;

import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceAuditService {
    private final BalanceAuditRepository balanceAuditRepository;

    private List<BalanceAudit> getAuditsAccount(Long accountId) {
       return balanceAuditRepository.findByAccountId(accountId);
    }
}
