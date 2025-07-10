package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "balance_audit")
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private Account account;

    @Column(name =  "authorization_balance", precision = 19, scale = 4, updatable = false)
    private BigDecimal authorizationBalance;

    @Column(name = "actual_balance", precision = 19, scale = 4, updatable = false)
    private BigDecimal actualBalance;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
}
