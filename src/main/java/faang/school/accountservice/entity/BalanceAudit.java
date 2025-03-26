package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "balance_audit")
public class BalanceAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "balance_id")
    private Long balanceId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "version")
    private int version;

    @Builder.Default
    @Column(name = "auth_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal authorizedBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "actual_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal actualBalance = BigDecimal.ZERO;

    @Column(name = "operation_id")
    private int operationId;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
