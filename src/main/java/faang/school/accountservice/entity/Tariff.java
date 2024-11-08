package faang.school.accountservice.entity;


import faang.school.accountservice.enums.TariffType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "tariff")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TariffType type;

    @ElementCollection
    @Column(name = "betting_value")
    private List<BigDecimal> bettingHistory;

    @OneToOne(mappedBy = "tariff", fetch = FetchType.LAZY)
    private SavingsAccount savingsAccount;

    @Column(name = "current_rate", nullable = false)
    private BigDecimal currentRate;
}