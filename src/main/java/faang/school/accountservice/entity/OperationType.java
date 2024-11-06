package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
@Entity
@Table(name = "operation_type")
public class OperationType {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "operationTypes")
    private List<CashbackTariff> cashbackTariffs;
}
