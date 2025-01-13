package faang.school.accountservice.service;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.entity.Transaction;
import faang.school.accountservice.repository.BalanceAuditRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceAuditService {
    private final BalanceAuditRepository balanceAuditRepository;

    @Transactional
    public void createAuditEntry(Account account, Transaction transaction) {
        log.info("Creating audit entry for Account ID: {} and Transaction ID: {}",
                account.getId(), transaction.getId());

        BalanceAudit balanceAudit = createBalanceAuditEntity(account, transaction);

        log.debug("BalanceAudit entity created: {}", balanceAudit);

        balanceAuditRepository.save(balanceAudit);

        log.info("Audit entry saved successfully for Account ID: {} and Transaction ID: {}",
                account.getId(), transaction.getId());
    }

    private BalanceAudit createBalanceAuditEntity(Account account, Transaction transaction) {
        Balance balance = account.getBalance();

        log.debug("Retrieved balance for Account ID: {}. Authorized: {}, Actual: {}",
                account.getId(), balance.getAuthorizedBalance(), balance.getActualBalance());

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
