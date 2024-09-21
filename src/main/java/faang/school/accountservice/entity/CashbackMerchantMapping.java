package faang.school.accountservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cashback_merchant_mapping")
public class CashbackMerchantMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "cashback_tariff_id", nullable = false)
    private CashbackTariff cashbackTariff;

    @Column(name = "merchant_name", nullable = false, length = 50)
    private String merchantName;

    @Column(name = "percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentage;
}
