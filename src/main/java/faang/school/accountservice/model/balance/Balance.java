package faang.school.accountservice.model.balance;

import faang.school.accountservice.model.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "balance")
@Entity
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Builder.Default
    @Column(name = "auth_balance")
    private BigDecimal authBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "current_balance")
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Version
    @Column(name = "version")
    private long version;

    @OneToMany(mappedBy = "balance")
    private List<AuthPayment> authorizationPayments;

//    @PrePersist
//    protected void onCreate() {
//        if (authBalance == null) {
//            authBalance = BigDecimal.ZERO;
//        }
//        if (currentBalance == null) {
//            currentBalance = BigDecimal.ZERO;
//        }
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }

//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
}
