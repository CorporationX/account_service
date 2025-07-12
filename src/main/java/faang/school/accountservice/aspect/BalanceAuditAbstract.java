package faang.school.accountservice.aspect;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.balance_audit.BalanceAuditService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@RequiredArgsConstructor
public abstract class BalanceAuditAbstract {
    private final BalanceAuditService balanceAuditService;
    private final BalanceAuditRepository balanceAuditRepository;

    public BalanceAudit saveBalanceAudit(UUID accountId) {
        Balance balance = balanceAuditService.searchBalance(accountId);

        BalanceAudit balanceAudit = new BalanceAudit();
        balanceAudit.setAccountNumber(balance.getAccount().getAccountNumber());
        balanceAudit.setAuthorizedAmount(balance.getAuthorizedAmount());
        balanceAudit.setActualAmount(balance.getActualAmount());
        balanceAudit.setCreatedAt(LocalDateTime.now());
        balanceAudit.setBalanceVersion(balance.getVersion().longValue());

        return balanceAuditRepository.save(balanceAudit);
    }
}
