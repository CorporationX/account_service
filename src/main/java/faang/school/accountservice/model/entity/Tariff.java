package faang.school.accountservice.model.entity;

import faang.school.accountservice.model.enums.TariffType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tariff")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tariff_type")
    @Enumerated(EnumType.STRING)
    private TariffType type;

    @OneToMany(mappedBy = "tariff")
    private List<TariffRate> tariffRates;

    @OneToOne()
    @JoinColumn(name = "tariff_rate_id")
    private TariffRate tariffRate;
}
