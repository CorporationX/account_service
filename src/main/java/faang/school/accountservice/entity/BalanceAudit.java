package faang.school.accountservice.entity;

import faang.school.accountservice.enums.AuditEventType;
import faang.school.accountservice.enums.BalanceAuditStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "balance_audit")
public class BalanceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(name = "audit_event_type", nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    private AuditEventType eventType;

    @Column(name = "current_auth_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentAuthAmount;

    @Column(name = "previous_auth_amount", precision = 19, scale = 2)
    private BigDecimal previousAuthAmount;

    @Column(name = "current_fact_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentFactAmount;

    @Column(name = "previous_fact_amount", precision = 19, scale = 2)
    private BigDecimal previousFactAmount;

    @Column(name = "audit_status", nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    private BalanceAuditStatus auditStatus;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "audited_at", nullable = false)
    private LocalDateTime auditedAt;
}
