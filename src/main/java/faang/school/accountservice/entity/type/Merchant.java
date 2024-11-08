package faang.school.accountservice.entity.type;

import faang.school.accountservice.entity.cashback.tariff.CashbackTariff;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
@Entity
@Table(name = "merchant")
public class Merchant implements CashbackMapping {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "merchants")
    private List<CashbackTariff> cashbackTariffs;
}
