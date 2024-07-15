package faang.school.accountservice.model;

import faang.school.accountservice.enums.Operation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "balance_audit")
public class BalanceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "account_id")
    private long accountId;

    @Builder.Default
    @Column(name = "authorization_balance")
    private BigDecimal authorizeBalance = new BigDecimal(0);

    @Builder.Default
    @Column(name = "actual_balance")
    private BigDecimal actualBalance = new BigDecimal(0);

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false)
    private Operation operation;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "version_balance")
    private Integer versionBalance;
}
