package faang.school.accountservice.entity.auth.payment;

import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.auth.payment.AuthPaymentStatus;
import faang.school.accountservice.enums.pending.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.ACTIVE;

@EqualsAndHashCode
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "auth_payment")
@Entity
public class AuthPayment {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "source_balance_id", nullable = false)
    private Balance sourceBalance;

    @Transient
    private Balance balance;

    @ManyToOne
    @JoinColumn(name = "target_balance_id")
    private Balance targetBalance;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AuthPaymentStatus status = ACTIVE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category = Category.OTHER;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Version
    @Column(name = "version")
    private long version;
}
