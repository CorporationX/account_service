package faang.school.accountservice.entity.cashback;

import faang.school.accountservice.entity.cashback.tariff.CashbackTariff;
import faang.school.accountservice.entity.type.OperationType;
import faang.school.accountservice.entity.cashback.id.CashbackId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(CashbackId.class)
@Table(name = "operation_cashback")
public class OperationCashback extends AbstractCashback<OperationType>{
    @Id
    @Column(name = "operation_id", nullable = false)
    private Long typeId;

    @Id
    @Column(name = "cashback_tariff_id", nullable = false)
    private Long tariffId;

    @JoinColumn(name = "cashback_tariff_id", nullable = false, updatable = false, insertable = false)
    @ManyToOne
    private CashbackTariff cashbackTariff;

    @ManyToOne
    @JoinColumn(name = "operation_id", nullable = false, insertable = false, updatable = false)
    private OperationType operationType;

    @Column(name = "cashback_percentage", nullable = false)
    private Double percentage;

    @Column(name = "version")
    @Version
    private Long version;
}
