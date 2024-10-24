package faang.school.accountservice.entity;


import faang.school.accountservice.enums.AccountRate;
import faang.school.accountservice.enums.TariffType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "savings_account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

    @Id
    @Positive
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TariffType type;

    @ElementCollection
    @CollectionTable(name = "tariff_betting_history", joinColumns = @JoinColumn(name = "tariff_id"))
    @Column(name = "betting_value")
    private List<Double> bettingHistory;

    @OneToOne
    @JoinColumn(name = "savings_account_id")
    private SavingsAccount savingsAccount;

    @Column(name = "current_rate", nullable = false)
    private Double currentRate;
}