package faang.school.accountservice.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tariff_rate")
public class TariffRate {

    @Id
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @Column(name = "rate")
    private double rate;
}
