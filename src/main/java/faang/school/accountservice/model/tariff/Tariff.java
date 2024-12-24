package faang.school.accountservice.model.tariff;

import faang.school.accountservice.model.interest_rate.InterestRate;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tariff")
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type", nullable = false)
    private TariffType tariffType;

    @ManyToOne
    @JoinColumn(name = "interest_rate_id")
    private InterestRate interestRate;

    @Column(name = "rate_history", columnDefinition = "TEXT", nullable = false)
    private String rateHistory;

    @OneToMany(mappedBy = "tariff")
    private List<SavingsAccount> savingsAccounts;

    @Column(name = "changed_by_user_history", columnDefinition = "TEXT", nullable = false)
    private String changedByUserHistory;
}