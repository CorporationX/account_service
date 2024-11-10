package faang.school.accountservice.entity.cashback;

import faang.school.accountservice.entity.cashback.tariff.CashbackTariff;
import faang.school.accountservice.entity.type.Merchant;
import faang.school.accountservice.entity.cashback.id.CashbackId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(CashbackId.class)
@Table(name = "merchant_cashback")
public class MerchantCashback extends AbstractCashback<Merchant> {
    @Id
    @Column(name = "merchant_id", nullable = false)
    private Long typeId;

    @Id
    @Column(name = "cashback_tariff_id", nullable = false)
    private Long tariffId;

    @JoinColumn(name = "cashback_tariff_id", nullable = false, updatable = false, insertable = false)
    @ManyToOne
    private CashbackTariff cashbackTariff;

    @JoinColumn(name = "merchant_id", nullable = false, updatable = false, insertable = false)
    @ManyToOne
    private Merchant merchant;

    @Column(name = "cashback_percentage", nullable = false)
    private Double percentage;

    @Column(name = "version")
    @Version
    private Long version;
}
