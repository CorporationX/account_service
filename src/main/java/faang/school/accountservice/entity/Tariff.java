package faang.school.accountservice.entity;

import faang.school.accountservice.enums.TariffType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.CascadeType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "tariff")
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tariff_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TariffType tariffType;

    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("effectiveDate ASC")
    private List<TariffRateHistory> rateHistory = new ArrayList<>();
}
