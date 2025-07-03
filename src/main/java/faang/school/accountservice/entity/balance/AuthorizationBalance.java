package faang.school.accountservice.entity.balance;

import faang.school.accountservice.entity.base.BaseEntityWithVersion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Entity
@Table(name = "authorization_balance")
public class AuthorizationBalance extends BaseEntityWithVersion {
    @ManyToOne
    @JoinColumn(name = "balance_id", nullable = false, updatable = false)
    private Balance balance;
    
    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;
    
    @Column(name = "type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private AuthorizationBalanceType type;
}
