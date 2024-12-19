package faang.school.accountservice.model.balance.audit;

import faang.school.accountservice.model.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "balance_audit")
public class BalanceAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version")
    private int version;

    @Column(name = "authorization_amount", nullable = false)
    private Long authorizationAmount;

    @Column(name = "actual_amount", nullable = false)
    private Long actualAmount;

    @Column(name = "operation_id", nullable = false)
    private long operationId;

    @Column(name = "created_at")
    private LocalDateTime created_at;
}
