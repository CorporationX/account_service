package faang.school.accountservice.entity.account;

import faang.school.accountservice.entity.owner.Owner;
import faang.school.accountservice.entity.tariff.Tariff;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "savings_accounts")
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String number;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @OneToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @Column(name = "tariff_history", columnDefinition = "TEXT")
    private String tariffHistory;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_interest_date")
    private LocalDateTime lastInterestDate;

    @Version
    private int version;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
