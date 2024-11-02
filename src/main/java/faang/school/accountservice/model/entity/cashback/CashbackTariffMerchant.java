package faang.school.accountservice.model.entity.cashback;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cashback_tariff__merchant_cashback")
public class CashbackTariffMerchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "percentage", nullable = false)
    private BigDecimal percentage;

    @ManyToOne
    @JoinColumn(name = "cashback_tariff_id")
    private CashbackTariff tariff;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private MerchantCashback merchant;
}
