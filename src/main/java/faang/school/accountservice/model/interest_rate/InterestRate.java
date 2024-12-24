package faang.school.accountservice.model.interest_rate;

import faang.school.accountservice.model.tariff.Tariff;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interest_rate")
public class InterestRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @OneToMany(mappedBy = "interestRate")
    private List<Tariff> tariffs;

    @Column(name = "changed_by_user_history", columnDefinition = "TEXT", nullable = false)
    private String changedByUserHistory;
}