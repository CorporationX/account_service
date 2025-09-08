package faang.school.accountservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;



@Entity
@Table(name = "balance")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", unique = true, nullable = false)
    private Account account;

    @NotNull
    @PositiveOrZero
    @Column(name = "authorized_balance", precision = 19, scale = 4, nullable = false)
    private BigDecimal authorizedBalance = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    @Column(name = "actual_balance", precision = 19, scale = 4, nullable = false)
    private BigDecimal actualBalance = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    // Конструкторы
    public Balance() {}

    public Balance(Account account) {
        this.account = account;
    }

    // Методы для операций с балансом
    public boolean authorizeAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (actualBalance.compareTo(amount) < 0) {
            return false; // Недостаточно средств
        }
        authorizedBalance = authorizedBalance.add(amount);
        actualBalance = actualBalance.subtract(amount);
        return true;
    }

    public void captureAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (authorizedBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("Not enough authorized funds");
        }
        authorizedBalance = authorizedBalance.subtract(amount);
    }

    public void reverseAuthorization(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (authorizedBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("Cannot reverse more than authorized");
        }
        authorizedBalance = authorizedBalance.subtract(amount);
        actualBalance = actualBalance.add(amount);
    }

    public void addFunds(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        actualBalance = actualBalance.add(amount);
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public BigDecimal getAuthorizedBalance() { return authorizedBalance; }
    public void setAuthorizedBalance(BigDecimal authorizedBalance) { this.authorizedBalance = authorizedBalance; }

    public BigDecimal getActualBalance() { return actualBalance; }
    public void setActualBalance(BigDecimal actualBalance) { this.actualBalance = actualBalance; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

}
