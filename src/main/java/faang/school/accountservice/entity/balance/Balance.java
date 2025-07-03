package faang.school.accountservice.entity.balance;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.base.BaseEntityWithVersion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "account")
public class Balance extends BaseEntityWithVersion {
    @OneToOne
    @JoinColumn(name = "account_id", nullable = false, unique = true, updatable = false)
    private Account account;
    // TODO: тип данных для баланса, чеки что не меньше нуля
    @Column(name = "authorized_balance", nullable = false)
    private BigDecimal authorizedBalance;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @OneToMany(mappedBy = "balance")
    private List<AuthorizationBalance> authorizationBalances = new ArrayList<>();
}
