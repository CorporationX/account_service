package faang.school.accountservice.model.cashback;

import faang.school.accountservice.mapper.CashbackMapping;
import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class AbstractCashback<T extends CashbackMapping> {
    @Id
    @Column(name = "cashback_tariff_id", nullable = false)
    private Long tariffId;

    @Id
    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @Column(name = "cashback_percentage", nullable = false)
    private Double percentage;

    @Column(name = "version")
    @Version
    private Long version;
}