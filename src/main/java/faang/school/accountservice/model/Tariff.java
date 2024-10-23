package faang.school.accountservice.model;

import faang.school.accountservice.model.enumeration.TariffType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

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

    @Column(name = "tariff_type", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private TariffType tariffType;

    @Builder.Default
    @OneToMany(mappedBy = "tariff", cascade = {PERSIST, MERGE})
    private List<TariffRate> tariffRates = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "tariff")
    private List<SavingsAccountTariff> savingsAccountTariffs = new ArrayList<>();

    public Double getCurrentRate() {
        return tariffRates.stream()
                .sorted(Comparator.comparing(TariffRate::getCreatedAt).reversed())
                .map(TariffRate::getRate)
                .findFirst()
                .orElseThrow();
    }
}
