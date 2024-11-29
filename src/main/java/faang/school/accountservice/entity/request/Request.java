/*
package faang.school.accountservice.entity.request;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "request")
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "UUID")
    private UUID id;

    private String context;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_task_id", nullable = false)
    private RequestTask requestTask;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "balance_id", nullable = false)
    private Balance balance;

//    @ManyToOne
//    @JoinColumn(name = "balance_audit_id", nullable = false)
//    private BalanceAudit balanceAudit;

    @PrePersist
    public void prePersist() {
        if(id == null) {
            id = UUID.randomUUID();
        }
    }
}
*/
