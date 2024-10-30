package faang.school.accountservice.model.entity;

import faang.school.accountservice.model.enums.Operation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "operation_type")
public class OperationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany
    @JoinTable(name = "cashback_tariff_operation_type",
            joinColumns = @JoinColumn(name = "operation_type_id"),
            inverseJoinColumns = @JoinColumn(name = "cashback_tariff_id")
    )
    private List<CashbackTariff> cashbackTariffs;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    private Operation operation;
}
