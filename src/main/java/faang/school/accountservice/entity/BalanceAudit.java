package faang.school.accountservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "balance_id", nullable = false, updatable = false)
    private Balance balance;

    @Column(name = "account_number", nullable = false, updatable = false, length = 20)
    @Size(min = 12, max = 20)
    private String accountNumber;

    @Column(name = "version", nullable = false, updatable = false)
    private Integer version;

    @Column(name = "authorization_balance", precision = 19, scale = 4, nullable = false, updatable = false)
    private BigDecimal authorizationBalance;

    @Column(name = "actual_balance", precision = 19, scale = 4, nullable = false, updatable = false)
    private BigDecimal actualBalance;

    @Column(name = "operation_id", nullable = false, updatable = false)
    private Long operationId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}