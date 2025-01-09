package faang.school.accountservice.model.cashback;

import jakarta.persistence.*;
import lombok.Setter;

@Setter
@Entity
@Table(name = "operation_cashback")
@IdClass(CashbackId.class)
public class OperationCashback extends AbstractCashback<OperationType> {
    @ManyToOne
    @JoinColumn(name = "cashback_tariff_id", insertable = false, updatable = false)
    private CashbackTariff cashbackTariff;

    @ManyToOne
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private OperationType operationType;
}