package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
@Entity
@Table(name = "merchant")
public class Merchant {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "merchants")
    private List<CashbackTariff> cashbackTariffs;
}
