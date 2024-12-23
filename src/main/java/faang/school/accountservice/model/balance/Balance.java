package faang.school.accountservice.model.balance;

import faang.school.accountservice.model.account.Account;
import jakarta.persistence.CascadeType;
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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "balance")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @Column(name = "authorized_value")
    private BigDecimal authorizedValue;

    @Column(name = "actual_value")
    private BigDecimal actualValue;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public void authorizePayment(BigDecimal value) {
        if (actualValue.subtract(authorizedValue).compareTo(value) < 0) {
            throw new IllegalStateException("Insufficient funds for transfer");
        }
        authorizedValue = authorizedValue.add(value);
        version++;
    }

    public void clearPayment(BigDecimal value) {
        if (authorizedValue.compareTo(value) < 0) {
            throw new IllegalStateException("Payment not authorized or already cleared");
        }
        actualValue = actualValue.subtract(value);
        authorizedValue = authorizedValue.subtract(value);
        version++;
    }

    public void receivePayment(BigDecimal value) {
        actualValue = actualValue.add(value);
        version++;
    }

    public void cancelAuthorization(BigDecimal value) {
        if (authorizedValue.compareTo(value) < 0) {
            throw new IllegalStateException("No such authorization exists");
        }
        authorizedValue = authorizedValue.subtract(value);
        version++;
    }
}