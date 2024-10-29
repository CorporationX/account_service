package faang.school.accountservice.service;

import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.balance.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceAuditService {

    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceAuditMapper balanceAuditMapper;

    @Transactional
    public void createBalanceAudit(Balance balance) {
        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(balance);
        balanceAudit.setVersion(balanceAudit.getVersion() + 1);

        balanceAuditRepository.save(balanceAudit);
    }

    @Transactional
    public void createBalanceAudit(Account account) {
        Balance balance = account.getBalance();
        balance.setAccount(account);
        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(balance);

        balanceAuditRepository.save(balanceAudit);
    }

}
