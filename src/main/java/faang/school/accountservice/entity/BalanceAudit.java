package faang.school.accountservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "balance_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "balance_version", nullable = false)
    private Integer balanceVersion;

    @Column(name = "authorized_balance", nullable = false)
    private BigDecimal authorizedBalance;

    @Column(name = "actual_balance", nullable = false)
    private BigDecimal actualBalance;

    @Column(name = "operation_id", nullable = false)
    private Long operationId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
