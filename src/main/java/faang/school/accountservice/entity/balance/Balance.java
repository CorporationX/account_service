package faang.school.accountservice.entity.balance;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Entity
@Table(name = "account")
public class Balance extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "account_id", nullable = false, unique = true, updatable = false)
    private Account account;

    @Column(name = "authorized_balance", nullable = false)
    private BigDecimal authorizedBalance;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
}
