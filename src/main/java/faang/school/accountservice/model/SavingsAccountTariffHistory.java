package faang.school.accountservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "savings_account_tariff_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountTariffHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savings_account_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_savings_account_tariff_history_savings_account_id"))
    private SavingsAccount savingsAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_savings_account_tariff_history_tariff_id"))
    private Tariff tariff;

    @Column(name = "start_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime startDate;

    @Column(name = "end_date")
    @UpdateTimestamp
    private LocalDateTime endDate;
}
