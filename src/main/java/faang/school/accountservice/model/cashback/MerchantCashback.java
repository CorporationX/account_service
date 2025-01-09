package faang.school.accountservice.model.cashback;

import jakarta.persistence.*;
import lombok.Setter;

@Setter
@Entity
@Table(name = "merchant_cashback")
@IdClass(CashbackId.class)
public class MerchantCashback extends AbstractCashback<Merchant> {
    @ManyToOne
    @JoinColumn(name = "cashback_tariff_id", insertable = false, updatable = false)
    private CashbackTariff cashbackTariff;

    @ManyToOne
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private Merchant merchant;
}