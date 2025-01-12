package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.entity.Transaction;
import faang.school.accountservice.repository.BalanceAuditRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceAuditService {
    private final BalanceAuditRepository balanceAuditRepository;
    @Transactional
    public void createAuditEntry(Account account, Transaction transaction) {
        BalanceAudit balanceAudit = createBalanceAuditEntity(account, transaction);
        balanceAuditRepository.save(balanceAudit);
    }

    private BalanceAudit createBalanceAuditEntity(Account account, Transaction transaction) {
        Balance balance = account.getBalance();

        return BalanceAudit.builder()
                .account(account)
                .balanceVersion(balance.getBalanceVersion())
                .authorizedBalance(balance.getAuthorizedBalance())
                .operationAmount(transaction.getTransactionAmount())
                .transaction(transaction)
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
