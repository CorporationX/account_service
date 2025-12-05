package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_balance", schema = "public")
@Getter
@NoArgsConstructor
public class AccountBalance {

    @Id
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AccountBalance(Account account) {
        this.account = account;
        this.accountId = account.getId();
        this.balance = BigDecimal.ZERO;
        this.version = 0L;
    }

    @Builder
    private AccountBalance(Long accountId, Account account, BigDecimal balance, Long version, LocalDateTime updatedAt) {
        this.accountId = accountId;
        this.account = account;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.version = version != null ? version : 0L;
        this.updatedAt = updatedAt;
    }
}

