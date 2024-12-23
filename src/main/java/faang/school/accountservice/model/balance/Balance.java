package faang.school.accountservice.model.balance;

import jakarta.persistence.*;
import faang.school.accountservice.model.account.Account;
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
    @MapsId
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

    public void cancelAuthorization(BigDecimal value) {
        if (authorizedValue.compareTo(value) < 0) {
            throw new IllegalStateException("No such authorization exists");
        }
        authorizedValue = authorizedValue.subtract(value);
        version++;
    }
}