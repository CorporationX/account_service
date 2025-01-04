package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceAuditService {
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceAuditMapper balanceAuditMapper;

    private List<BalanceAudit> getAuditsAccount(Long accountId) {
       return balanceAuditRepository.findByAccountId(accountId);
    }

    @Transactional
    public void createBalanceAudit(Account account, Long transactionId) {
        Balance balance = account.getBalance();
        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(balance, transactionId);
        balanceAuditRepository.save(balanceAudit);
    }
}
