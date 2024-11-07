package faang.school.accountservice.entity;

import faang.school.accountservice.entity.id.MerchantCashbackId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(MerchantCashbackId.class)
@Table(name = "merchant_cashback")
public class MerchantCashback  {
    @Id
    @Column(name = "cashback_tariff_id", nullable = false)
    private Long tariffId;

    @Id
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @JoinColumn(name = "cashback_tariff_id", nullable = false, updatable = false, insertable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private CashbackTariff cashbackTariff;

    @JoinColumn(name = "merchant_id", nullable = false, updatable = false, insertable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private Merchant merchant;

    @Column(name = "cashback_percentage", nullable = false)
    private Double percentage;

    @Column(name = "version")
    @Version
    private Long version;
}
