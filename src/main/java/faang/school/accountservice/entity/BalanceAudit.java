package faang.school.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "balance_audit")
public class BalanceAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_number", length = 20, nullable = false, unique = true, updatable = false)
    private String accountNumber;
    @JoinColumn(name = "balance_version", nullable = false)
    private Long balanceVersion;
    @Column(name = "authorized_amount", precision = 30, scale = 10, nullable = false)
    private BigDecimal authorizedAmount;
    @Column(name = "actual_amount", precision = 30, scale = 10, nullable = false)
    private BigDecimal actualAmount;
    @JoinColumn(name = "balance_change_id")
    private Long balanceChangeId;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}