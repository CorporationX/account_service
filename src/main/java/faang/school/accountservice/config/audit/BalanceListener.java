/*
package faang.school.accountservice.config.audit;


import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.entity.balance.BalanceAudit;
import faang.school.accountservice.repository.balance.BalanceAuditRepository;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Configurable
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class BalanceListener extends AuditingEntityListener{

    private final BalanceAuditRepository balanceAuditRepository;

    @Autowired
    private EntityManager entityManager;

    @PrePersist
    public void prePersist(Balance balance) {
        // Можно выполнять действия перед созданием записи
    }

    @PostUpdate
    public void postUpdate(Balance balance) {
        BalanceAudit audit = BalanceAudit.builder()
                .account(balance.getAccount())
                .authorizationBalance(balance.getAuthorizationBalance())
                .actualBalance(balance.getActualBalance())
                .operationId(1)
                .build();
       // entityManager.persist(audit);
         balanceAuditRepository.save(audit);
    }

    @PostPersist
    public void postPersist(Balance balance) {
        BalanceAudit audit = BalanceAudit.builder()
                .account(balance.getAccount())
                .authorizationBalance(balance.getAuthorizationBalance())
                .actualBalance(balance.getActualBalance())
                .operationId(1)
                .build();

          balanceAuditRepository.save(audit);
    }
}
*/
