package faang.school.accountservice.entity.tariff;

import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.entity.rate.Rate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tariffs")
public class Tariff {

    @Id
    @Column(name = "tariff_name", length = 24, nullable = false, unique = true)
    private String tariffName;

    @OneToOne
    @JoinColumn(name = "rate_id")
    private Rate rate;

    @Column(name = "rate_history", columnDefinition = "TEXT")
    private String rateHistory;

    @OneToMany(mappedBy = "tariff")
    private List<SavingsAccount> savingsAccounts;
}
