package faang.school.accountservice.model.entity;

import faang.school.accountservice.model.enums.TariffType;
import jakarta.persistence.*;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Entity
@Table(name = "tariff")
public class Tariff {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "tariff_type")
    private TariffType type;

    @OneToMany(mappedBy = "tariff")
    private List<TariffRate> tariffRates;
}
