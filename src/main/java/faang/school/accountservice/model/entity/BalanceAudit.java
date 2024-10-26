package faang.school.accountservice.model.entity;

import faang.school.accountservice.model.enums.OperationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "balance_audit")
public class BalanceAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "balance_id")
    private Balance balance;

    @Column(name = "balance_version")
    private long version;

    @Column(name = "current_authorization_balance", precision = 18, scale = 2)
    private BigDecimal authorizationBalance;

    @Column(name = "current_actual_balance", precision = 18, scale = 2)
    private BigDecimal actualBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    private OperationType type;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "audit_at")
    private LocalDateTime auditAt;

    @PrePersist
    public void prePersist() {
        if (balance != null) {
            this.version = balance.getVersion();
            this.authorizationBalance = balance.getCurrentAuthorizationBalance();
            this.actualBalance = balance.getCurrentActualBalance();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BalanceAudit that)) return false;
        return id == that.id && version == that.version && Objects.equals(balance, that.balance)
                && Objects.equals(authorizationBalance, that.authorizationBalance)
                && Objects.equals(actualBalance, that.actualBalance)
                && type == that.type
                && Objects.equals(auditAt, that.auditAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, version, authorizationBalance, actualBalance, type, auditAt);
    }
}
