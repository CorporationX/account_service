package faang.school.accountservice.model.cashback;

import faang.school.accountservice.mapper.CashbackMapping;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "merchant")
public class Merchant implements CashbackMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "merchant_cashback",
            joinColumns = @JoinColumn(name = "merchant_id"),
            inverseJoinColumns = @JoinColumn(name = "cashback_tariff_id")
    )
    private List<CashbackTariff> cashbackTariffs;
}