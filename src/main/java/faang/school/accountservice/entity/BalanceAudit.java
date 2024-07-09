package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "balance_audit")
public class BalanceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "balance_id")
    private Balance balance;

    @Column(name = "balance_version", nullable = false)
    private long balanceVersion;

    @Column(name = "authorization_balance", nullable = false)
    private BigDecimal currentAuthorizationBalance;

    @Column(name = "actual_balance", nullable = false)
    private BigDecimal currentActualBalance;

    @Column(name = "operation_id", nullable = false)
    private long operationId;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
