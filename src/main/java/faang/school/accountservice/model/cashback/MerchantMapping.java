package faang.school.accountservice.model.cashback;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "merchant_mapping")
public class MerchantMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashback_tariff_id", nullable = false)
    private CashbackTariff cashbackTariff;

    @Column(name = "merchant_id", nullable = false)
    private String merchantId;

    @Column(name = "cashback_percentage", nullable = false)
    private BigDecimal cashbackPercentage;

    @Column(name = "category", nullable = false)
    private String category;
}