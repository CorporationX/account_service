package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Entity
@Immutable
@Table(name = "balance_audit")
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "balance_id", nullable = false)
    private Balance balance;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "authorization_balance", precision = 19, scale = 4, nullable = false)
    private BigDecimal authorizationBalance;

    @Column(name = "actual_balance", precision = 19, scale = 4, nullable = false)
    private BigDecimal actualBalance;

    @Column(name = "operation_id", nullable = false)
    private Long operationId;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
}