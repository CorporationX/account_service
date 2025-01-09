package faang.school.accountservice.model.cashback;

import faang.school.accountservice.mapper.CashbackMapping;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "operation_type")
public class OperationType implements CashbackMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "operation_cashback",
            joinColumns = @JoinColumn(name = "operation_id"),
            inverseJoinColumns = @JoinColumn(name = "cashback_tariff_id")
    )
    private List<CashbackTariff> cashbackTariffs;
}