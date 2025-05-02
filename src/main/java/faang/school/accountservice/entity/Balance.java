package faang.school.accountservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "balance")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "balance_balance_id_seq")
    @SequenceGenerator(name = "balance_balance_id_seq", sequenceName = "balance_balance_id_seq", allocationSize = 1)
    @Column(name = "balance_id")
    private Long balanceId;

    @NotNull
    @OneToOne
    @JoinColumn(name = "account_number", nullable = false)
    private Account account;

    @NotNull
    @Column(name = "authorization_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal authorizationBalance;

    @NotNull
    @Column(name = "factual_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal factualBalance;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @NotNull
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
