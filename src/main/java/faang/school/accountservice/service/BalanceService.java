package faang.school.accountservice.service;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceAuditMapper balanceAuditMapper;

    public Balance createBalance(Balance balance, Long operationId) {
        Balance savedBalance = balanceRepository.save(balance);
        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(savedBalance, operationId);
        balanceAuditRepository.save(balanceAudit);

        return savedBalance;
    }
    @Transactional
    public Balance updateBalance(Balance balance, Long operationId) {
        Balance updatedBalance = balanceRepository.save(balance);
        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(updatedBalance, operationId);
        balanceAuditRepository.save(balanceAudit);

        return updatedBalance;
    }
}
