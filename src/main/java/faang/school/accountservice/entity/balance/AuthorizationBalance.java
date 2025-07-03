package faang.school.accountservice.entity.balance;

import faang.school.accountservice.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "authorization_balance")
public class AuthorizationBalance extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "balance_id", nullable = false, updatable = false)
    private Balance balance;

    @Column(name = "balance", precision = 30, scale = 10, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private AuthorizationBalanceType type;

    @Column(name = "expires_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expiresAt;
}
