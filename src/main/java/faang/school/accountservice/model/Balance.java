package faang.school.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "balance")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "actual_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal actualBalance = BigDecimal.ZERO;

    @Column(name = "authorized_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal authorizedBalance = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public void authorize(BigDecimal amount) {
        actualBalance = actualBalance.subtract(amount);
        authorizedBalance = authorizedBalance.add(amount);
    }

    public void clear(BigDecimal amount) {
        authorizedBalance = authorizedBalance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        actualBalance = actualBalance.add(amount);
    }

    public void cancelAuthorization(BigDecimal amount) {
        authorizedBalance = authorizedBalance.subtract(amount);
        actualBalance = actualBalance.add(amount);
    }
}
