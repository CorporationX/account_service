package faang.school.accountservice.entity;

import faang.school.accountservice.entity.id.OperationCashbackId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(OperationCashbackId.class)
@Table(name = "operation_cashback")
public class OperationCashback {
    @Id
    @Column(name = "cashback_tariff_id", nullable = false)
    private Long tariffId;

    @Id
    @Column(name = "operation_id", nullable = false)
    private Long operationId;

    @JoinColumn(name = "cashback_tariff_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private CashbackTariff cashbackTariff;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "operation_id", nullable = false, insertable = false, updatable = false)
    private OperationType operationType;

    @Column(name = "cashback_percentage", nullable = false)
    private double percentage;
}
